package com.me.socket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class SecondClient {
    private final String host;
    private final int port;

    public SecondClientHandler cl=new SecondClientHandler();

    public SecondClient() {
        this(0);
    }

    public SecondClient(int port) {
        this("localhost", port);
    }

    public SecondClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect(String msg) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group) // 注册线程池
                    .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
                    .remoteAddress(new InetSocketAddress(this.host, this.port)) // 绑定连接端口和host信息
                    .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("--------------------client: channel log begin--------------------");
                            System.out.println("正在连接中...");
                            ch.pipeline().addLast(new StringEncoder(Charset.forName("GBK")));
//                            ch.pipeline().addLast(new SecondClientHandler());
                            ch.pipeline().addLast(cl);
                            ch.pipeline().addLast(new ByteArrayEncoder());
                            ch.pipeline().addLast(new ChunkedWriteHandler());

                        }
                    });
            // System.out.println("服务端连接成功..");

            ChannelFuture cf = b.connect().sync(); // 异步连接服务器
            System.out.println("服务端连接成功..."); // 连接完成

            cl.myctx.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8)); // 必须有flush

            cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
            System.out.println("连接已关闭.."); // 关闭完成

        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
        }
    }

    public static void main(String[] args) throws Exception {

        new SecondClient("127.0.0.1", 8888).connect("second msg"); // 连接127.0.0.1/65535，并启动

    }
}