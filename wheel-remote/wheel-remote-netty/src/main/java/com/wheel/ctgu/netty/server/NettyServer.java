package com.wheel.ctgu.netty.server;


import com.wheel.ctgu.netty.client.Constants;
import com.wheel.ctgu.netty.codec.RpcDecoder;
import com.wheel.ctgu.netty.codec.RpcEncoder;
import com.wheel.ctgu.rpc.core.common.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Author: changjiu.wang
 * @Date: 2021/7/24 22:22
 */
@Slf4j
public class NettyServer{





    // 底层会启动2*cpu个数的NioEventLoop，轮询注册到对应的NioEventLoop运行
    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup();
    // 全局复用，可共享
    private NettyServerHandler serverHandler = new NettyServerHandler();
    private int port;

    public NettyServer(int port) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // boss线程池用于accept到达的请求，worker线程池对到达的请求进行读写
        // child表示对到达的请求起作用，没有child表示对ServerSocketChannel起作用
        // 服务端用NioServerSocketChannel
        ChannelFuture channelFuture;
        this.port = port;
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // option最终设置到jdk sever channel上
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 对到达的请求进行读写操作，责任链模式，ChannelPipeline
                            ch.pipeline()
                                    .addLast(new RpcDecoder())
                                    .addLast(new RpcEncoder())
                                    // 空闲触发器，服务端需要是客户端的2倍或以上，复用netty io线程，容易受到影响，可以单独搞个线程池
                                    .addLast(new IdleStateHandler(Constants.HEARTBEAT_TIMEOUT_MILLIS, 0, 0, TimeUnit.MILLISECONDS))
                                    .addLast(serverHandler);
                        }
                    });
            // bind时，会新建NioServerSocketChannel，并注册到NioEventLoop.selector中
            // 底层转换为pipeline.bind()，最终调用serverSocketChannel.bind(socketAddress, 128);
            channelFuture = serverBootstrap.bind(port);
            // 下面会阻塞
            channelFuture.sync();
            log.info("服务器绑定端口：{}成功", port);
        } catch (Exception e) {
            throw new RuntimeException("bind port error:" + port, e);
        }
    }


    /**
     * 关闭时调用，客户端也得关闭
     * dubbo shutdown hook
     */
    public void close() {
        log.info("关闭端口：{}", port);
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
