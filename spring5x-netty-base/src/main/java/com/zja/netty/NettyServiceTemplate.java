package com.zja.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**Socket服务器和Http服务器的使用时的些许差异,只是ChannelHandler不同
 * Date: 2019-12-09 10:49
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：公共的服务器配置模板NettyServiceTemplate,这个模板既可以被tcp使用，也可以被http使用
 */
public abstract class NettyServiceTemplate {
    //bossGroup和workerGroup就是为了建立线程池
    static private EventLoopGroup bossGroup = new NioEventLoopGroup();
    static private EventLoopGroup workerGroup = new NioEventLoopGroup();

    abstract protected ChannelHandler[] createHandlers();

    abstract public int getPort();

    abstract public String getName();

    @PostConstruct
    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelHandler[] handlers = createHandlers();
                        for (ChannelHandler handler : handlers) {
                            ch.pipeline().addLast(handler);
                        }
                    }
                }).option(ChannelOption.SO_BACKLOG, 128).option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.SO_REUSEADDR, true);

        //ChannelHandler使用抽象方法由子类去实现，这样就可以根据不同的ChannelHandler实现不同的功能
        ChannelFuture cf = b.bind(getPort()).await();
        //阻塞了当前主线程一直等待结束，后面的代码就不能执行了
        // cf.channel().closeFuture().await();
        if (!cf.isSuccess()) {
            System.out.println("无法绑定端口：" + getPort());
            throw new Exception("无法绑定端口：" + getPort());
        }

        System.out.println("服务[{" + getName() + "}]启动完毕，监听端口[{" + getPort() + "}]");
    }

    @PreDestroy
    public void stop() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
        System.out.println("服务[{" + getName() + "}]关闭。");
    }
}
