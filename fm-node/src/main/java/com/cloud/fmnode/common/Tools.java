package com.cloud.fmnode.common;

import javax.servlet.http.HttpServletResponse;
import javax.tools.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 说明：常用工具
 */
public class Tools {
	protected static Logger logger = Logger.getLogger(Tools.class);
	
	/**
	 * 随机生成六位数验证码 
	 * @return
	 */
	public static int getRandomNum(){
		 Random r = new Random();
		 return r.nextInt(900000)+100000;//(Math.random()*(999999-100000)+100000)
	}
	
	/**
	 * 随机生成四位数验证码 
	 * @return
	 */
	public static int getRandomNum4(){
		 Random r = new Random();
		 return r.nextInt(9000)+1000;
	}
	
	/**
	 * 检测字符串是否不为空(null,"","null")
	 * @param s
	 * @return 不为空则返回true，否则返回false
	 */
	public static boolean notEmpty(String s){
		return s!=null && !"".equals(s) && !"null".equals(s);
	}
	
	/**
	 * 检测字符串是否为空(null,"","null")
	 * @param s
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean isEmpty(String s){
		return s==null || "".equals(s) || "null".equals(s);
	}
	
	/**
	 * 字符串转换为字符串数组
	 * @param str 字符串
	 * @param splitRegex 分隔符
	 * @return
	 */
	public static String[] str2StrArray(String str,String splitRegex){
		if(isEmpty(str)){
			return null;
		}
		return str.split(splitRegex);
	}
	
	/**
	 * 用默认的分隔符(,)将字符串转换为字符串数组
	 * @param str	字符串
	 * @return
	 */
	public static String[] str2StrArray(String str){
		return str2StrArray(str,",\\s*");
	}
	
	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，日期转字符串
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String date2Str(Date date){
		return date2Str(date,"yyyy-MM-dd HH:mm:ss");
	}

	public static String getCurrTimeStr(){
		Date date = new Date();
		return Tools.date2Str(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String getCurrTimeStrFileName(){
		Date date = new Date();
		return Tools.date2Str(date, "yyyyMMddHHmmss");
	}
	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
	 * @param date
	 * @return
	 */
	public static Date str2Date(String date){
		if(notEmpty(date)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new Date();
		}else{
			return null;
		}
	}
	
	/**
	 * 按照参数format的格式，日期转字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date2Str(Date date,String format){
		if(date!=null){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}else{
			return "";
		}
	}
	
	/**
	 * 把时间根据时、分、秒转换为时间段
	 * @param StrDate
	 */
	public static String getTimes(String StrDate){
		String resultTimes = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now;
	    try {
	    	now = new Date();
	    	Date date=df.parse(StrDate);
	    	long times = now.getTime()-date.getTime();
	    	long day  =  times/(24*60*60*1000);
	    	long hour = (times/(60*60*1000)-day*24);
	    	long min  = ((times/(60*1000))-day*24*60-hour*60);
	    	long sec  = (times/1000-day*24*60*60-hour*60*60-min*60);
	        
	    	StringBuffer sb = new StringBuffer();
	    	//sb.append("发表于：");
	    	if(hour>0 ){
	    		sb.append(hour+"小时前");
	    	} else if(min>0){
	    		sb.append(min+"分钟前");
	    	} else{
	    		sb.append(sec+"秒前");
	    	}
	    	resultTimes = sb.toString();
	    } catch (ParseException e) {
	    	e.printStackTrace();
	    }
	    return resultTimes;
	}
	
	/**
	 * 往文件里的内容
	 * @param fileP  文件路径
	 * @param content  写入的内容
	 */
	public static void writeFile(String fileP,String content){
		String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""));	//项目路径
		filePath = filePath.replaceAll("file:/", "");
		filePath = filePath.replaceAll("%20", " ");
		filePath = filePath.trim() + fileP.trim();
		if(filePath.indexOf(":") != 1){
			filePath = File.separator + filePath;
		}
		try {
	        OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(filePath),"utf-8");      
	        BufferedWriter writer=new BufferedWriter(write);          
	        writer.write(content);      
	        writer.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 往文件里的内容（ClassResource下）
	 * @param fileP  文件路径
	 * @param content  写入的内容
	 */
	public static void writeFileCR(String fileP,String content){
		String filePath = PathUtil.getClassResources() + fileP;
		try {
	        OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(filePath),"utf-8");      
	        BufferedWriter writer=new BufferedWriter(write);          
	        writer.write(content);      
	        writer.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	  * 验证邮箱
	  * @param email
	  * @return
	  */
	 public static boolean checkEmail(String email){
	  boolean flag = false;
	  try{
	    String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	    Pattern regex = Pattern.compile(check);
	    Matcher matcher = regex.matcher(email);
	    flag = matcher.matches();
	   }catch(Exception e){
	    flag = false;
	   }
	  return flag;
	 }
	
	 /**
	  * 验证手机号码
	  * @param mobileNumber
	  * @return
	  */
	 public static boolean checkMobileNumber(String mobileNumber){
	  boolean flag = false;
	  try{
	    Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
	    Matcher matcher = regex.matcher(mobileNumber);
	    flag = matcher.matches();
	   }catch(Exception e){
	    flag = false;
	   }
	  return flag;
	 }
	 
	/**
	 * 检测KEY是否正确
	 * @param paraname  传入参数
	 * @param FKEY		接收的 KEY
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean checkKey(String paraname, String FKEY){
		paraname = (null == paraname)? "":paraname;
		return MD5.md5(paraname+DateUtil.getDays()+",fh,").equals(FKEY);
	}

	public static String getFilePath(String path){
		String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""));	//项目路径
		filePath = filePath.replaceAll("file:/", "");
		filePath = filePath.replaceAll("%20", " ");
		filePath = filePath.trim() + path.trim();
		if(filePath.indexOf(":") != 1){
			filePath = File.separator + filePath;
		}

		return filePath;
	}

	public static File getFile(String path){
		String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""));	//项目路径
		filePath = filePath.replaceAll("file:/", "");
		filePath = filePath.replaceAll("%20", " ");
		filePath = filePath.trim() + path.trim();
		if(filePath.indexOf(":") != 1){
			filePath = File.separator + filePath;
		}
		String encoding = "utf-8";
		File file = new File(filePath);

		return file;
	}
	 
	/**
	 * 读取txt里的单行内容
	 * @param fileP  文件路径
	 */
	public static String readTxtFile(String fileP) {
		try {
			String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""));	//项目路径
			filePath = filePath.replaceAll("file:/", "");
			filePath = filePath.replaceAll("%20", " ");
			filePath = filePath.trim() + fileP.trim();
			if(filePath.indexOf(":") != 1){
				filePath = File.separator + filePath;
			}
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { 		// 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), encoding);	// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					return lineTxt;
				}
				read.close();
			}else{
				System.out.println("找不到指定的文件,查看此路径是否正确:"+filePath);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}
		return "";
	}

	public static String readTxtFile(File file) {
		StringBuffer fileContent = new StringBuffer();
		try {
			String encoding = "utf-8";
			if (file.isFile() && file.exists()) { 		// 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);	// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					fileContent.append(lineTxt);
					fileContent.append("\n");
				}
				read.close();
			}else{
				System.out.println("找不到指定的文件,查看此路径是否正确:");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}

		return fileContent.toString();
	}
	
	/**读取txt里的全部内容
	 * @param fileP  文件路径
	 * @param encoding  编码
	 * @return
	 */
	public static String readTxtFileAll(String fileP, String encoding) {
		StringBuffer fileContent = new StringBuffer(); 
		try {
			String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
			String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource(""));	//项目路径
			filePath = filePath.replaceAll("file:/", "");
			filePath = filePath.replaceAll("%20", " ");
			filePath = filePath.trim() + fileP.trim();
			if(filePath.indexOf(":") != 1){
				filePath = File.separator + filePath;
			}
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { 		// 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), encoding);	// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					fileContent.append(lineTxt);
					fileContent.append("\n");
				}
				read.close();
			}else{
				System.out.println("找不到指定的文件,查看此路径是否正确:"+filePath);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}
		return fileContent.toString();
	}
	
	/**
	 * 读取ClassResources某文件里的全部内容
	 * @param fileP  文件路径
	 */
	public static String readFileAllContent(String fileP) {
		StringBuffer fileContent = new StringBuffer(); 
		try {
			String encoding = "utf-8";
			File file = new File(PathUtil.getClassResources() + fileP);//文件路径
			if (file.isFile() && file.exists()) { 		// 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), encoding);	// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					fileContent.append(lineTxt);
					fileContent.append("\n");
				}
				read.close();
			}else{
				System.out.println("找不到指定的文件,查看此路径是否正确:"+fileP);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}
		return fileContent.toString();
	}

	public static void addTreeNodeButton(Map treeNode, String id, String name){
		List buttons = (List)treeNode.get("buttons");
		if(buttons == null){
			buttons = new ArrayList();
		}

		Map button = new HashMap();
		button.put("id", id);
		button.put("name", name);
		buttons.add(button);

		treeNode.put("buttons", buttons);
	}

	public static List<String> getVariableList(String var, boolean innerParam){
		String regex = "\\$\\{[A-Za-z0-9\\._]+\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(var);
		List<String> matchParamList = new ArrayList<String>();
		while (m.find()) {
			String param = m.group();
			if(innerParam){
				param = param.substring(2, param.length() - 1);
			}
			matchParamList.add(param);
		}

		return matchParamList;
	}

	public static List<String> getVariableList(String var){
		return getVariableList(var, false);
	}

	public static List<String> getVariableList2(String var){
		String regex = "\\$<[A-Za-z0-9\\._]+>";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(var);
		List<String> matchParamList = new ArrayList<String>();
		while (m.find()) {
			matchParamList.add(m.group());
		}

		return matchParamList;
	}

	public static List<String> getRegex(String var, String regex){
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(var);
		List<String> matchParamList = new ArrayList<String>();
		while (m.find()) {
			matchParamList.add(m.group());
		}

		return matchParamList;
	}

	public static void cloneArrayListMap(List from, List to){
		for(Object fromItemObj : from){
			Map fromItemMap = (Map)fromItemObj;
			HashMap newItem = new HashMap();
			newItem.putAll(fromItemMap);
			to.add(newItem);
		}
	}

	public static <T> List<T> deepCopy(List<T> src)
			throws IOException, ClassNotFoundException
	{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		return (List<T>)in.readObject();
	}

	public static ResultCode errorLogMsg(String msg){
		StringBuilder errMsg = new StringBuilder();
		errMsg.append(Thread.currentThread().getStackTrace()[2].getClassName()).append("[").append(Thread.currentThread().getStackTrace()[2].getLineNumber()).append("] ").append(msg);
		logger.error(errMsg.toString());

		return ResultCode.getFailure(msg);
	}

	public static void errorLogMsg(String msg, Throwable e){
		errorLogMsg(msg);

		e.printStackTrace();
	}


	/**
	 * 功能描述: 发送响应流方法
	 *
	 * @param
	 * @return
	 * @auther mazhen
	 * @date 2018/12/11 下午2:17
	 */
	public static void setResponseHeader(HttpServletResponse response, String fileName) {
		try {
			try {
				fileName = new String(fileName.getBytes(), "ISO8859-1");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("excel导出错误：" + e);
			}
			response.setContentType("application/octet-stream;charset=ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.addHeader("Pargam", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("excel导出错误：" + ex);
		}
	}

	public static void putAllDelNull(Map target, Map source){
		for(Object item: source.entrySet()){
			Map.Entry entry = (Map.Entry) item;
			Object value = entry.getValue();
			if("null".equals(value) || "".equals(value)){
				continue;
			}
			target.put(entry.getKey(), value);
		}
	}
	public static void main(String[] args) {
		System.out.println(getRandomNum());
	}

	public static List<String> getRGB(String rgb){
		List<String> ret = new ArrayList<>();
		if(rgb.startsWith("#")){
			rgb = rgb.substring(1);
		}
		if(rgb.length() != 6){
			return ret;
		}

		try {
			int first = Integer.parseInt(rgb.substring(0, 2), 16);
			int second = Integer.parseInt(rgb.substring(2, 4), 16);
			int third = Integer.parseInt(rgb.substring(4, 6), 16);
			ret.add("" + first);
			ret.add("" + second);
			ret.add("" + third);
		}catch (Exception e){
			e.printStackTrace();
		}

		return ret;
	}

	public static ResultCode execJava(String javaStr){

		List<String> javaOptions = ClassOptions.getClassOptionsList();
		//获取编译器
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//获取文件管理器
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		StrSrcJavaObject object = new StrSrcJavaObject("com.fh.util.Test666", javaStr);
		ForwardingJavaFileManager javaFileManager = new MyJavaFileManager(fileManager);
		Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(object);
		JavaCompiler.CompilationTask task = compiler.getTask(null, javaFileManager, null, javaOptions, null, fileObjects);
		Boolean reslult = task.call();
		if (Objects.isNull(reslult) || !reslult.booleanValue()) {
			throw new RuntimeException("编译出错");
		}
// 获取编译后的字节码
		Map<String, byte[]> bytes= object.getBytes();

		MyClassLoader myclassLoader = new MyClassLoader(bytes);
		try {
			Class cla = myclassLoader.loadClass("com.fh.util.Test666");
			Object obj = cla.newInstance();
			Method method = cla.getMethod("test", null);
			method.invoke(obj, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return ResultCode.getSuccess();
	}
}
