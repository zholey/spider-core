/**
 * 版权所有@2016 北京京投亿雅捷交通科技有限公司；
 * 未经许可，不得擅自复制、传播；
 */
package com.biierg.spider.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 提供JavaScript引擎相关的工具方法
 * 
 * @author lei
 */
public class JSEngineUtil {

	private static ScriptEngine engine;

	static {
		engine = new ScriptEngineManager().getEngineByName("JavaScript");
	}

	/**
	 * 将Java方法开放到JS环境内，供JS调用
	 * 
	 * @param name
	 *            开放至JS环境内的方法名
	 * @param consumer
	 *            准备开放的Java方法
	 */
	public static void bind(String name, Object consumer) {
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

		if (bindings == null) {
			engine.createBindings();
			engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
		}
		bindings.put(name, consumer);
	}

	/**
	 * 将给定的脚本加载进入脚本引擎
	 * 
	 * @param script
	 * @throws ScriptException
	 * @throws IOException
	 */
	public static void loadScript(String script) throws ScriptException, IOException {
		engine.eval(script);
	}

	/**
	 * 将给定的脚本文件加载进入脚本引擎
	 * 
	 * @param scriptFilePath
	 * @throws ScriptException
	 * @throws IOException
	 */
	public static void loadScript(Path scriptFilePath) throws ScriptException, IOException {
		engine.eval(Files.newBufferedReader(scriptFilePath));
	}

	/**
	 * 调用JS方法
	 * 
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws ScriptException
	 */
	public static Object invoke(String methodName, Object... args) throws NoSuchMethodException, ScriptException {
		return ((Invocable) engine).invokeFunction(methodName, args);
	}
}
