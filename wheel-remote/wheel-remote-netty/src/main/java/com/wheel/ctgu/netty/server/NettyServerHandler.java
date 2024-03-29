package com.wheel.ctgu.netty.server;


import com.wheel.ctgu.common.exporter.WheelExporter;
import com.wheel.ctgu.common.invoker.RpcInvocation;
import com.wheel.ctgu.common.utils.StringUtils;
import com.wheel.ctgu.rpc.core.common.RpcRequest;
import com.wheel.ctgu.rpc.core.common.RpcResponse;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 允许注册到多个客户端SocketChannel中
 */
@ChannelHandler.Sharable
@Slf4j
public class NettyServerHandler extends ChannelDuplexHandler {

    private Map<Channel, Channel> clientChanel = new ConcurrentHashMap<>();

    private ExecutorService executorService = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(100), new ThreadFactory() {
        AtomicInteger id = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            String threadName = "miniWheel_server_biz_thread_" + id.incrementAndGet();
            return new Thread(r, threadName);
        }
    });

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端:{}和服务端建立连接成功", ctx.channel().remoteAddress());
        // 这里维护所有的客户端，长时间没有数据到达时进行关闭
        clientChanel.put(ctx.channel(), ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("收到客户端退出的消息");
        clientChanel.remove(ctx.channel());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 已经解码了
        RpcRequest request = (RpcRequest) msg;
        RpcResponse response = new RpcResponse();
        response.setId(request.getId());
        if (request.isEvent()) {
            log.info("收到心跳请求消息：{}，进行响应", msg);
            // 通过原来客户端通道发送出去，这里会走编码
            response.setEvent(true);
            ctx.writeAndFlush(response);
            return;
        }
        // 这里交给线程池处理，避免阻塞NioEventLoop
        CompletableFuture.supplyAsync(() -> {
            processBizRequest(ctx, msg);
            return null;
        }, executorService);
    }

    private void processBizRequest(ChannelHandlerContext ctx, Object msg) {
        RpcRequest request = (RpcRequest) msg;
        RpcResponse response = new RpcResponse();
        response.setId(request.getId());
        log.info("收到请求消息：{}", msg);
        RpcInvocation rpcInvocation = request.getRpcInvocation();
        Object obj = WheelExporter.getService(rpcInvocation);
        if (obj == null) {
            response.setException(true);
            response.setResult(StringUtils.toString(new RuntimeException("no provider")));
            // 通过原来客户端通道发送出去，这里会走编码
            ctx.writeAndFlush(response);
            return;
        }
        try {
            log.info("开始反射调用：{}", msg);
            // 这里所有参数都是正确的，有泛化信息也是对的，由序列化层解决
            Method method = obj.getClass().getMethod(rpcInvocation.getMethodName(), rpcInvocation.getParameterType());
            log.info("入参：{}", rpcInvocation.getArgs());
            // filter...
            Object responseData = method.invoke(obj, rpcInvocation.getArgs());
            if (responseData != null && CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
                // 服务端如果返回的是future类型，最终返回的是future具体的值
                ((CompletableFuture) responseData).whenComplete((result, exception) -> {
                    if (exception != null) {
                        response.setException(true);
                        response.setResult(StringUtils.toString((Throwable)exception));
                    } else {
                        response.setResult(result);
                    }
                    // 通过原来客户端通道发送出去，这里会走编码
                    ctx.writeAndFlush(response);
                });
                return;
            }
            response.setResult(responseData);
            log.info("调用实例：{}，方法：{}，返回结果：{}",
                    obj, method, response);

        } catch (Exception e) {
            log.error("调用Wheel异常：{}", rpcInvocation, e);
            response.setException(true);
            response.setResult(StringUtils.toString(e));
        }
        // 通过原来客户端通道发送出去，这里会走编码
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("IO出错了...", cause);
    }

    /**
     * 写回调
     *
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("发起写请求：{}", msg);
        super.write(ctx, msg, promise);
    }

    /**
     * 服务端检测到某个client channel空闲时，直接关闭。客户端会定时发起心跳，如果超过一定时间没有数据到达，
     * 说明客户端挂了或者网络有问题，需要关闭客户端
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info("超时时间到达，关闭客户端：{}", ctx.channel());
            clientChanel.remove(ctx.channel());
            ctx.channel().close();
        }
        super.userEventTriggered(ctx, evt);
    }
}