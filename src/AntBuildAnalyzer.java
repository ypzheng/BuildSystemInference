import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

public class AntBuildAnalyzer implements BuildFileAnalyzer{
	private Vector sortedTargets;
	private ArrayList<Target> potentialSrcTargets, potentialTestTargets;
	private Target compileSrcTarget, compileTestTarget;
	
	
	public AntBuildAnalyzer(File f) {
		//Initialize Variables
		compileSrcTarget = null;
		compileTestTarget = null;
		potentialSrcTargets = new ArrayList<Target>();
		potentialTestTargets = new ArrayList<Target>();
		
		Project project = new Project();
		project.init();
		ProjectHelper helper = new ProjectHelper();
		helper.configureProject(project, f);
		sortedTargets = project.topoSort(project.getDefaultTarget(), project.getTargets());
		
		//Print out all targets in execution order
		Enumeration vEnum = sortedTargets.elements();
	    System.out.println("Targets sorted in order of execution:");
	    while(vEnum.hasMoreElements())
	    		System.out.print(vEnum.nextElement() + "\n");
	    this.getPotentialCompileTargets();
	}

	/**
	 * If a target contains javac: check if the target name contains "test",
	 * add it to the list that contains potential compile-test targets; if it does
	 * not contain "test", add it to the list that contains potential compile-source
	 * target.
	 * 
	 */
	private void getPotentialCompileTargets() {
		Enumeration vEnum = sortedTargets.elements();
		while(vEnum.hasMoreElements()) {
			Target t = (Target) vEnum.nextElement();
			if(containsJavac(t.getTasks())) {
				if(t.getName().contains("test")) {
					potentialTestTargets.add(t);
					System.out.println("test: "+t.getName());
				}
				else{
					potentialSrcTargets.add(t);
					System.out.println("src: "+t.getName());
				}
			}
		}
	}
	
	/**
	 * Helpter method that checks if a task contains javac.
	 * @param tasks
	 * @return
	 */
	private boolean containsJavac(Task[] tasks) {
		for(Task t : tasks) {
			if(t.getTaskType().equals("javac")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get compile-source target from the potential targets array.
	 * TODO: If the array is empty, we should check if the buid file is valid.
	 * If the array contains exactly one element, it is the target we are looking for.
	 * If the array contains more than one element, we can find compile-source target
	 * at the end of the list, since the list is sorted in execution order.
	 * @return
	 */
	public String getCompileSrcTarget() {
		int size = potentialSrcTargets.size();
		if(size == 0) {
			System.out.println("Cannot find target that compiles source");
			return "";
		}
		else if(size == 1) {
			compileSrcTarget = potentialSrcTargets.get(0);
		}
		else if(size > 1) {
			compileSrcTarget = potentialSrcTargets.get(size - 1);
		}
		
		return compileSrcTarget.getName();
	}
	
	/**
	 * Similar to the method above.
	 * TODO: Need to consider the case when the compile-test target doesn't contain
	 * the key word "test".  For example, TestBuildFile1 compiles test and source 
	 * together in the target "compile".s
	 * @return
	 */
	public String getCompileTestTarget() {
		int size = potentialTestTargets.size();
		if(size == 0) {
			return "";
		}
		else if(size == 1) {
			compileTestTarget = potentialTestTargets.get(0);
		}
		else if(size > 1) {
			compileTestTarget = potentialTestTargets.get(size - 1);
		}
		System.out.println("test target: "+compileTestTarget.getName());
		return compileTestTarget.getName();
	}

	@Override
	public String getSrcDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTestDir() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Find source directory of compilation
	 * 
	 * IF directory is not found return an empty String
	 * 
	 * @return String
	 */
	@Override
	public String getCompSrcDir() {
		//Compile Target is not found yet
				if(compileSrcTarget == null) {
					
					//Cannot find Compile Target
					if(getCompileSrcTarget().equals("")) {
						return "";
					}
				} 
				
				//Infer Src Directory from Compile Target
				/**
				 * Find "javac" Task
				 * Looks for "srcdir" attribute
				 */
				Task[] tasks = compileSrcTarget.getTasks();
				for(Task t : tasks) {
					if(t.getTaskType().equals("javac")) {
						RuntimeConfigurable rt =t.getRuntimeConfigurableWrapper();
						Hashtable att_map = rt.getAttributeMap();
						
						String srcDirectory = (String) att_map.get("srcdir");
						if(srcDirectory == null) {
							return "";
						}else {
							return srcDirectory;
						}
					}
				}
				return "";
	}

	@Override
	public String getCompTestDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSrcDep() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTestDep() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTestList() {
		// TODO Auto-generated method stub
		return null;
	}
}
