package com.me.socket.client;

import com.me.dto.TransportObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FirstClient {

    private final String host;
    private final int port;

    public FirstClientHandler cl=new FirstClientHandler();

    public FirstClient() {
        this(0);
    }

    public FirstClient(int port) {
        this("localhost", port);
    }

    public FirstClient(String host, int port) {
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
//                            ch.pipeline().addLast(new FirstClientHandler());
                            ch.pipeline().addLast(cl);
                            ch.pipeline().addLast(new ByteArrayEncoder());
                            ch.pipeline().addLast(new ChunkedWriteHandler());

                        }
                    });
            // System.out.println("服务端连接成功..");

            ChannelFuture cf = b.connect().sync(); // 异步连接服务器
            System.out.println("服务端连接成功..."); // 连接完成

//            channel = cf.channel();

            cl.myctx.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8)); // 必须有flush

            cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
            System.out.println("连接已关闭.."); // 关闭完成

        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
        }
    }

    public static void main(String[] args) throws Exception {

        TransportObject data = new TransportObject();
        data.setId(999);
        data.setName("xyz");
        List<String> list = new ArrayList<String>();
        list.add("111");
        list.add("222");
        list.add("333");
        data.setList(list);

        // 8882: second-server, transfer data to second-server
        FirstClient firstClient = new FirstClient("127.0.0.1", 8882);
        firstClient.connect(data.toString());

        System.out.println("=========================");

    }
}