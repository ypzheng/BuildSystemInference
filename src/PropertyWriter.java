import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyWriter {
	
	private BuildFileAnalyzerAdapter adapter;
	private static File file;
	private Properties propertyFile;

	public PropertyWriter(String type, File buildFile, File outputFile, String projectName) throws IOException {

		adapter = new BuildFileAnalyzerAdapter(type, buildFile, projectName);
		file = outputFile;
		propertyFile = new Properties();
		this.setProperties();
	}
	
	
	private void setProperties() throws IOException {

		propertyFile.setProperty("src.compile", adapter.getCompileSrcTarget());
		propertyFile.setProperty("test.compile", adapter.getCompileTestTarget());
		propertyFile.setProperty("src.dir", adapter.getSrcDir());
		propertyFile.setProperty("src.comp.dir", adapter.getCompDir());
		propertyFile.setProperty("test.dir", adapter.getTestDir());
		propertyFile.setProperty("test.comp.dir", adapter.getCompTestDir());
		saveProperties(propertyFile);
		//	loadProperties(propertyFile);
	
	}
	
	public static void saveProperties(Properties p)throws IOException{
		
		FileOutputStream fr=new FileOutputStream(file);	
		p.store(fr,"Properties");
	    fr.close();
	}
	
	public static void loadProperties(Properties p)throws IOException{
		
		FileInputStream fi=new FileInputStream("src/default.properties");
		p.load(fi);
		fi.close();
		System.out.println(p.getProperty("component.package"));
	}
}

