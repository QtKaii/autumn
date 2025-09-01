package logging; //Requires to be in the package logging

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 
 * @author lukeraeside This logger class must be used throughout your assignment
 *         each time Log every method entry by placing logMethodEntry(this) at
 *         the beginning of every method Log every method exit by placing
 *         logMethodExit(this) at the end of every method
 * 
 *         For static methods use logStaticMethodEntry() and
 *         logStaticMethodExit() This is important as these methods cannot pass
 *         a reference 'this'
 *         
 *         For main call logMain()
 * 
 *         For constructors use logConstructor(Object classId)
 * @version 3 Feb 2024
 * @
 * 
 * 
 */
public class AssignmentLogger {

	static String hourly = "HH"; //Set this to format for hourly log
	static String minuteByminute = "HH_mm"; //Set this to format for minute by minute log
	static String constant = "HH_mm_ss"; //Set this to format for continuous logs
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern(hourly);
	static LocalDateTime now = LocalDateTime.now();
	static Logger log = Logger.getLogger("External file log");
	static Handler fileOut = createExternalLogFile();
	static int detailsGenerated = 0;


	/**
	 * Initialise the logger with the class to be logged
	 */
	public static synchronized void logInitialise(Object classId) {
		log.log(Level.INFO, "\n" + getDetails(classId) + "Initialise Logger from main\n");
	}	
	
	/**
	 * Makes a log entry at the start of the method
	 */
	public static synchronized void logMethodEntry(Object classId) {
		log.log(Level.INFO, "\n" + getDetails(classId) + "Method entry\n");
	}

	/**
	 * Makes a log entry at the end of the method
	 */
	public static synchronized void logMethodExit(Object classId) {
		log.log(Level.INFO, "\n" + getDetails(classId) + "Method exit\n");
	}

	/**
	 * Make a log for a static method entry No parameter as there is no object
	 * created
	 */
	public synchronized static void logStaticMethodEntry() {
		log.log(Level.INFO, "\n" + "\nStatic method entry: static calls generate minimal logs, call logConstructor(this) in constructor to log full class details - must be after any 'super' calls" + "\n");
	}

	/**
	 * Make a log for a static method exit No parameter as there is no object
	 * created
	 */
	public synchronized static void logStaticMethodExit() {
		log.log(Level.INFO, "\n" + "\nStatic method exit: static calls generate minimal logs, call logConstructor(this) in constructor to log full class details - must be after any 'super' calls" + "\n");
	}

	/**
	 * Makes a log entry at the beginning of a constructor
	 */
	public static synchronized void logConstructor(Object classId) {
		log.log(Level.INFO, "\n" + getDetails(classId) + " Constructor call \n");
	}

	/**
	 * Makes a log entry at the start of a main method
	 */
	public static synchronized void logMain() {
		log.log(Level.INFO, "\n" + "\nMain method call: static calls generate minimal logs, call logConstructor(this) in constructor to log full class details - must be after any 'super' calls" + "\n");
	}

	/**
	 * Makes a log entry at every catch error
	 */
	public static synchronized void logCatchException(Exception e) {
		log.log(Level.INFO, "\nException " + e.toString() + " " + e.getMessage() + "\n");
	}

	/**
	 * This method sets up the external file handler
	 * 
	 * @return Handler the file handler for the external file log
	 */
	private static synchronized Handler createExternalLogFile() {

		try {
			if (fileOut == null) {
				fileOut = new FileHandler(System.getProperty("user.name") + "_" + dtf.format(now) + "_logDetails.txt");
				fileOut.setFormatter(new SimpleFormatter());
			}

			log.addHandler(fileOut);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileOut;
	}

	/**
	 * Returns most details for use in the logger
	 * 
	 * @param classId The class passed to the logger
	 * @return String of all of the details produced by the logger
	 */
	private static String getDetails(Object classId) {
		if (detailsGenerated<1) {
			detailsGenerated++;
						
			return "\n----Machine and version details----\n\n" + classId.getClass().getCanonicalName() + "\nRan from: " + System.getProperty("user.dir")
					+ "\nRan by: " + System.getProperty("user.name") + "\nOS Details:" + System.getProperty("os.name")
					+ " " + System.getProperty("os.arch") + "\nJava details:" + System.getProperty("java.version") + " "
					+ System.getProperty("java.home") + "\n\n" + "----Assignment Summary Details---- \n\n" + getSummaryData(classId) + "\n" + getAdvancedLogDetails(classId) + "\n----LIST OF RECENT METHOD CALLS---\n\n";
		}
		else {
			detailsGenerated++;
			return "";
		}

	}
	
	/**
	 * This method will show the amount of features in the class in total
	 * @param classId This is the class being examined
	 * @return This is the String with the summary data returned
	 */
	public static String getSummaryData(Object classId) {
		String constructorTotal = "Total Constructors = " + classId.getClass().getDeclaredConstructors().length + "\n";
		String fieldsTotal = "Total Fields = " + classId.getClass().getDeclaredFields().length + "\n";
		String methodsTotal = "Total Methods = " + classId.getClass().getDeclaredMethods().length;
		return constructorTotal + fieldsTotal + methodsTotal;	
	}

	/**
	 * Make a call to create a log file with advanced details about the classes
	 * created
	 * 
	 * @param classId The class that was passed
	 * @return Sends a log of advanced information about the code
	 */

	public static String getAdvancedLogDetails(Object classId) {
		String completeString = getMethodDetails(classId) + getFieldDetails(classId) + getConstructorDetails(classId);
		return completeString;
	}

	/**
	 * This method will send to the advanced log file all of the details of the code
	 * created It includes the methods and parameters and return types etc.
	 * 
	 * @param classId The class that was passed
	 */
	private static String getFieldDetails(Object classId) {
		String completeString="";
		Vector<String> fieldNames = new Vector<String>();
		Vector<String> fieldTypes = new Vector<String>();
		Vector<String> fieldAccess = new Vector<String>();

		Field[] fields = classId.getClass().getDeclaredFields();

		completeString += "\n\n ----FULL FIELDS DETAILS----\n\n" + "TOTAL COUNT=" + fields.length;

		for (int i = 0; i < fields.length; i++) {
			fieldNames.add(fields[i].getName());
			fieldTypes.add(fields[i].getType().getCanonicalName());
			fieldAccess.add(decodeModifier(fields[i].getModifiers()));

			completeString += "\n***FIELD " + (i + 1) + "\n" + fieldAccess.elementAt(i) + " " + fieldTypes.elementAt(i)
					+ " " + fieldNames.elementAt(i);
		}
		return completeString;
	}

	/**
	 * This method will send to the advanced log file all of the details of the code
	 * created It includes the fields, field types etc.
	 * 
	 * @param classId The class that was passed
	 */
	private static String getMethodDetails(Object classId) {

		String completeString="";
		Vector<String> methodNames = new Vector<String>();
		Vector<String> methodReturnTypes = new Vector<String>();
		Vector<String> methodAccess = new Vector<String>();
		Vector<String> methodParameters = new Vector<String>();

		Method[] methods = classId.getClass().getDeclaredMethods();
		
		completeString += "\n ----FULL METHOD DETAILS---- \n\n" + "TOTAL METHODS = " + methods.length +"\n";

		for (int i = 0; i < methods.length; i++) {
			methodNames.add(methods[i].getName());
			methodReturnTypes.add(methods[i].getReturnType().getCanonicalName());
			methodAccess.add(decodeModifier(methods[i].getModifiers()));
			methodParameters.add(decodeParameters(methods[i].getParameters()));

			completeString += "\n***METHOD " + (i + 1) + "\n" + methodAccess.elementAt(i) + " " + methodReturnTypes.elementAt(i)
							+ " " + methodNames.elementAt(i) + " " + methodParameters.elementAt(i);
		}
		return completeString;

	}

	/**
	 * This method will add to the logs any constructor details
	 * 
	 * @param classId The class to be examined
	 */
	private static String getConstructorDetails(Object classId) {
		String completeString="";
		Vector<String> constructorNames = new Vector<String>();
		Vector<String> constructorParameters = new Vector<String>();

		Constructor[] constructors = classId.getClass().getConstructors();
		
		completeString+="\n\n----FULL CONSTRUCTOR(S) DETAILS----\n\n" + "TOTAL COUNT=" + constructors.length;

		for (int i = 0; i < constructors.length; i++) {
			constructorNames.add(constructors[i].getName());
			constructorParameters.add(decodeParameters(constructors[i].getParameters()));

			completeString += "\n***CONSTRUCTOR " + (i + 1) + "\n" + constructorNames.elementAt(i) + " "
					+ constructorParameters.elementAt(i) + "\n";

		}
		return completeString;
	}

	/**
	 * Decodes the modifier to readable code
	 * 
	 * @param mod The modifier in integer form
	 * @return The modifier in string form public, private, protected, package
	 *         (default)
	 */
	private static String decodeModifier(int mod) {

		if (Modifier.isPublic(mod)) {
			return "public";
		} else if (Modifier.isPrivate(mod)) {
			return "private";
		} else if (Modifier.isProtected(mod)) {
			return "protected";
		} else {
			return "package";
		}
	}

	/**
	 * Decodes the modifier to readable code
	 * 
	 * @param mod The modifier in integer form
	 * @return The modifier in string form public, private, protected, package
	 *         (default)
	 */
	private static String decodeParameters(Parameter[] array) {

		String parameterDetails = "";

		if (array.length < 1) {
			return "No-parameters";
		} else {
			for (int i = 0; i < array.length; i++) {
				parameterDetails += "\n" + "Parameter " + (i+1) + " is type ";
				if(array[i].getType().getName().charAt(0)=='[') {
					if(!array[i].isVarArgs()) {
						//This is an array
						String subString = "";
						subString="\\[" + "L";
						parameterDetails += " Array of " + array[i].getType().getName().replaceFirst(subString, "");
					}
					else {
						parameterDetails += "Vararg of type" + array[i].getType().getName();
					}
				}
				else {
					parameterDetails += array[i].getType().getName();
				}
			}
		}

		return parameterDetails;
	}

}
