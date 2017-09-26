package demo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

	public static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	public static  Map<String,Channel> map = new HashMap<String,Channel>();

@Override
protected void channelRead0(ChannelHandlerContext arg0, String arg1)
        throws Exception {
    Channel channel = arg0.channel();
    
    Pattern p = Pattern.compile("\\s*|\t|\r|\n");  
    Matcher m = p.matcher(arg1);  
    String msg = m.replaceAll(""); 
    
    if(msg.equals("xiaobai")||msg.equals("xiaohei")) {
    	map.put(msg, channel);
    }
    if(arg1.contains(",")) {
    	
    	String[] result = arg1.split(",");
    	msg = result[1];
    	
    	String toid = result[0];
    	Channel toUser=map.get(toid);
    	toUser.writeAndFlush("某人对你说"+msg);
    }    
    /*for (Channel ch : group) {
        if (ch == channel) {
            ch.writeAndFlush("[you]：" + arg1 + "\n");
        } else {
            ch.writeAndFlush(
                    "[" + channel.remoteAddress() + "]: " + arg1 + "\n");
        }
    }*/

}

@Override
public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    for (Channel ch : group) {
        ch.writeAndFlush(
                "[" + channel.remoteAddress() + "] " + "is comming");
    }
    group.add(channel);
}

@Override
public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    for (Channel ch : group) {
        ch.writeAndFlush(
                "[" + channel.remoteAddress() + "] " + "is leving");
    }
    group.remove(channel);
}

@Override
public void channelActive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    System.out.println("[" + channel.remoteAddress() + "] " + "online");
}

@Override
public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    Channel channel = ctx.channel();
    System.out.println("[" + channel.remoteAddress() + "] " + "offline");
}

@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception {
    System.out.println(
            "[" + ctx.channel().remoteAddress() + "]" + "exit the room");
    ctx.close().sync();
}
}

