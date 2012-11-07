package bjd.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import bjd.Kernel;
import bjd.net.OneBind;
import bjd.option.Conf;
import bjd.option.OneOption;
import bjd.server.OneServer;
import bjd.util.IDispose;
import bjd.util.Util;

public final class OnePlugin implements IDispose {
	private File file;
	private String classNameOption;
	private String classNameServer;

	public OnePlugin(File file, String classNameOption, String classNameServer) {
		this.file = file;
		this.classNameOption = classNameOption;
		this.classNameServer = classNameServer;
	}

	@Override
	public void dispose() {
		
	}

	public String getName() {
		return classNameOption;
	}

	/**
	 * classオブジェクトの作成
	 * @param file ファイル
	 * @param className　クラス名
	 * @param args パラメータの型配列
	 * @return
	 * @throws Exception
	 */
	private Constructor createConstructor(File file, String className, Class[] args) throws Exception {
		try {
			URL url = file.getCanonicalFile().toURI().toURL();
			URLClassLoader loader = new URLClassLoader(new URL[] { url });
			Class cobj = loader.loadClass(className);
			return cobj.getConstructor(args);
		} catch (IOException e) {
			throw new Exception("IOException");
		} catch (ClassNotFoundException e) {
			throw new Exception("ClassNotFoundException");
		} catch (NoSuchMethodException e) {
			throw new Exception("NoSuchMethodException");
		} catch (SecurityException e) {
			throw new Exception("SecurityException");
		}
	}

	/**
	 * Optionインスタンスの生成
	 * @param kernel
	 * @return
	 */
	public OneOption createOption(Kernel kernel) {
		try {
			 //URL url = file.getCanonicalFile().toURI().toURL();
             //URLClassLoader loader = new URLClassLoader( new URL[] { url });
           	 //Class cobj = loader.loadClass(classNameOption);
             
             //TODO Debug Print
             //System.out.println(String.format("OnePlugin() cobj = %s",cobj));
             //return null;
			
			Constructor constructor = createConstructor(file, classNameOption, new Class[] { Kernel.class, String.class });
			return (OneOption) constructor.newInstance(new Object[] { kernel, file.getPath() });
		} catch (Exception e) {
			//何の例外が発生しても、プラグインとしては受け付けない
			Util.runtimeException(e.getMessage()/*e.getClass().getName()*/);
			return null;
		}
	}

	/**
	 * Serverインスタンスの生成
	 * @param kernel
	 * @param conf
	 * @param oneBind
	 * @return
	 */
	public OneServer createServer(Kernel kernel, Conf conf, OneBind oneBind) {
		try {
			Constructor constructor = createConstructor(file, classNameServer, new Class[] { Kernel.class, Conf.class, OneBind.class });
			return (OneServer) constructor.newInstance(new Object[] { kernel, conf, oneBind });
		} catch (Exception e) {
			//何の例外が発生しても、プラグインとしては受け付けない
			Util.runtimeException(e.getMessage());
			return null;
		}
	}

	
//	private OneOption createOption(File file, String className, Kernel kernel) {
//		try {
//			URL url = file.getCanonicalFile().toURI().toURL();
//			URLClassLoader loader = new URLClassLoader(new URL[] { url });
//			Class cobj = loader.loadClass(className);
//			Constructor constructor = cobj.getConstructor(new Class[] { Kernel.class, String.class });
//			return (OneOption) constructor.newInstance(new Object[] { kernel, file.getPath() });
//		} catch (Exception e) {
//			//何の例外が発生しても、プラグインとしては受け付けない
//			return null;
//		}
//	}

}
