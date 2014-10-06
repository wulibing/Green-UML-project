package edu.buffalo.cse.greenest.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ASTCreation {
	
	IPackageFragment[] _packages;
	ICompilationUnit[] _icomp;
	
	public void Parse () throws JavaModelException {
		
		String project = null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		
		//Somehow single out a single IProject that we want to use that is referenced by project below.
		// IProject project;
		
		IJavaProject javaProject = (IJavaProject) JavaCore.create(project);
		IPackageFragment[] _packages = javaProject.getPackageFragments();
		correctPackages(_packages);
		
		//Find the correct ICompilationUnit.
		
		
		
	}
	//This methods checks that the packages are Source Files.
	public void correctPackages(IPackageFragment[] pack) throws JavaModelException {
		for (IPackageFragment mypackage : pack) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				_icomp = mypackage.getCompilationUnits();
				parse(_icomp);				
			}
		}
		
		
	}
	private void parse(ICompilationUnit[] icomp) {
		for (ICompilationUnit comp : icomp) {
			createAST(comp);
			
		}
		
		
	}
	private CompilationUnit createAST(ICompilationUnit comp) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(comp);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

}
