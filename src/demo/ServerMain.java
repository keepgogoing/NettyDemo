package demo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;    
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerMain {

private int port;

public ServerMain(int port) {
    this.port = port;
}

public static void main(String[] args) {
    new ServerMain(8000).run();
}

public void run() {
    EventLoopGroup acceptor = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
    bootstrap.group(acceptor, worker);//设置循环线程组，前者用于处理客户端连接事件，后者用于处理网络IO
    bootstrap.channel(NioServerSocketChannel.class);//用于构造socketchannel工厂
    bootstrap.childHandler(new ServerIniterHandler());//为处理accept客户端的channel中的pipeline添加自定义处理函数
    try {
        Channel channel = bootstrap.bind(port).sync().channel();//绑定端口（实际上是创建serversocketchannnel，并注册到eventloop上），同步等待完成，返回相应channel
        System.out.println("server strart running in port:" + port);
        channel.closeFuture().sync();//在这里阻塞，等待关闭
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        //优雅退出
        acceptor.shutdownGracefully();
        worker.shutdownGracefully();
        }
    }

}

