package com.biierg.spider;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.biierg.spider.function.JSFunction;
import com.biierg.spider.jmx.RobotOption;
import com.biierg.spider.model.Site;
import com.biierg.spider.support.JSEngineUtil;
import com.biierg.spider.support.SpiderMap;

/**
 * 人员定位API信息获取
 * 
 * @author lei
 */
public class SpiderApp {
	private static Logger logger = null;

	private ApplicationContext appContext = null;
	private ScheduledExecutorService executeService;
	
	/**
	 * 入口
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		logger = initLog4j("./etc/log4j2.xml");

		// enable jmx
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName mbeanName = new ObjectName("com.biierg.spider.jmx:type=RobotOption");
			final RobotOption robotOption = new RobotOption();
			mbs.registerMBean(robotOption, mbeanName);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		
		new SpiderApp().startup("/spring-context.xml");
	}
	
	public void startup(String springContextLocation) {
		
		if (logger == null) {
			logger = LoggerFactory.getLogger(SpiderApp.class);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("启动 爬虫 ...");
		}
		appContext = new ClassPathXmlApplicationContext(springContextLocation);

		try {
			Map<String, Object> jsFuncMap = appContext.getBeansWithAnnotation(JSFunction.class);
			if (jsFuncMap != null && !jsFuncMap.isEmpty()) {
				jsFuncMap.keySet().forEach(jsFuncName -> {
					JSEngineUtil.bind(jsFuncName, jsFuncMap.get(jsFuncName));
				});
			}

			// 解析站点配置
			List<Site> sites = SpiderMap.getInstance().getSites();
			if (sites != null && !sites.isEmpty()) {
				executeService = Executors.newScheduledThreadPool(sites.size() < 10 ? sites.size() : 10);

				sites.forEach(site -> {
					appContext.getBean(Robot.class, executeService, site).startup();
				});
			}

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 根据指定的配置文件，初始化日志输出器
	 *
	 * @param logConfFile
	 * @return
	 */
	public static Logger initLog4j(String logConfFile) {

		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(Paths.get(logConfFile).toUri());

		// 重新初始化Log4j2的配置上下文
		context.reconfigure();

		return LoggerFactory.getLogger(SpiderApp.class);
	}

	/**
	 * 注册扩展 Classpath
	 *
	 * @param extClasspth
	 *            扩展classpath
	 */
	public static void registerExtClasspath(String extClasspth) {
		String extClasspath = System.getProperty("user.dir") + extClasspth;
		File extClasspathDir = new File(extClasspath);
		if (extClasspathDir.exists()) {
			// 查找ClassLoader及其addURL方法
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			Method addUrlMethod = null;
			try {
				addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
				if (addUrlMethod != null) {
					addUrlMethod.setAccessible(true);
				}
			} catch (Throwable e) {
				logger.error("在添加外部扩展CLASSPATH[{}]时出现异常", extClasspathDir.getPath());
			}
			if (addUrlMethod != null) {
				try {
					// 将extClasspathDir加入CLASSPATH
					addUrlMethod.invoke(classLoader, new Object[] { extClasspathDir.toURI().toURL() });
					if (logger.isInfoEnabled()) {
						logger.info("append extClasspath:{}", extClasspathDir.getPath());
					}
				} catch (Throwable e) {
					logger.error("在添加外部扩展CLASSPATH[{}]时出现异常", extClasspathDir.getPath());
				}
			} else {
				logger.error("在给定的类加载器内未找到addURL方法");
			}
		}
	}
}
