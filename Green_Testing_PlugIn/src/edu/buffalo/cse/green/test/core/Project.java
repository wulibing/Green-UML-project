/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Erich Gamma (erich_gamma@ch.ibm.com) and
 * 	   Kent Beck (kent@threeriversinstitute.org)
 *******************************************************************************/

package edu.buffalo.cse.green.test.core;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameRequestor;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * The default project (for tests). This code was taken from an Eclipse plugin
 * development book. See above.
 * 
 * @author bcmartin
 */
public class Project {
    private IProject project;

    private IJavaProject javaProject;

    private IPackageFragmentRoot sourceFolder;

    public Project(String projectName, IProjectDescription description,
    		boolean newProject) throws CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        project = root.getProject(projectName);
        
        if (description != null) {
        	project.create(description, null);
        } else {
        	project.create(null);
        }

        javaProject = JavaCore.create(project);

        project.open(null);
        setJavaNature();

        if (newProject) {
        	try {
        		IFolder binFolder = createBinFolder();
        		javaProject.setRawClasspath(new IClasspathEntry[0], null);
        		createOutputFolder(binFolder);
        	} catch (CoreException e) {
        		// do nothing
        	}		
         	
            addSystemLibraries();
        }
        
    }

    /**
     * Returns the default project.
     */
    public IProject getProject() {
        return project;
    }

    /**
     * Returns the default project as an <code>IJavaProject</code>.
     */
    public IJavaProject getJavaProject() {
        return javaProject;
    }

    /**
     * Creates a package within the default project.
     */
    public IPackageFragment createPackage(String name) throws CoreException {
        if (sourceFolder == null)
            sourceFolder = createSourceFolder();
        return sourceFolder.createPackageFragment(name, false, null);
    }

    /**
     * Creates a compilation unit within the specified package with the
     * specified name.
     */
    public IType createType(IPackageFragment pack, String cuName, String source)
            throws JavaModelException {
        StringBuffer buf = new StringBuffer();
        buf.append("package " + pack.getElementName() + ";\n");
        buf.append("\n");
        buf.append(source);
        ICompilationUnit cu = pack.createCompilationUnit(cuName,
                buf.toString(), false, null);
        return cu.getTypes()[0];
    }

    /**
     * Called when the test quits.
     */
    public void dispose() throws CoreException {
        waitForIndexer();
        project.delete(true, true, null);
    }

    /**
     * Creates the BIN folder for the project.
     */
    private IFolder createBinFolder() throws CoreException {
        IFolder binFolder = project.getFolder("bin");
        binFolder.create(false, true, null);
        return binFolder;
    }

    /**
     * Sets the Java nature of the project.
     */
    private void setJavaNature() throws CoreException {
        IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[] { JavaCore.NATURE_ID });
        project.setDescription(description, null);
    }

    /**
     * Sets the default output folder to be the BIN folder.
     */
    private void createOutputFolder(IFolder binFolder)
            throws JavaModelException {
        javaProject.setOutputLocation(binFolder.getFullPath(), null);
    }

    /**
     * Creates the SRC folder for the project.
     */
    private IPackageFragmentRoot createSourceFolder() throws CoreException {
        IFolder folder = project.getFolder("src");
        folder.create(false, true, null);
        IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);

        IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
        javaProject.setRawClasspath(newEntries, null);
        return root;
    }

    /**
     * Adds the appropriate system libraries to the project.
     */
    private void addSystemLibraries() throws JavaModelException {
        IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        newEntries[oldEntries.length] = JavaRuntime
                .getDefaultJREContainerEntry();
        javaProject.setRawClasspath(newEntries, null);
    }

    /**
     * Waits for idle.
     */
    private void waitForIndexer() throws JavaModelException {
        new SearchEngine().searchAllTypeNames(null, null,
                SearchPattern.R_EXACT_MATCH, SearchPattern.R_CASE_SENSITIVE,
                SearchEngine.createJavaSearchScope(new IJavaElement[0]),
                new TypeNameRequestor() {}, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
    }
}