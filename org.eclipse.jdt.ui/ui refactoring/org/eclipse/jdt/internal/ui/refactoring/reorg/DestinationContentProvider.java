/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.refactoring.reorg;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.refactoring.reorg.IReorgDestinationValidator;

import org.eclipse.jdt.ui.StandardJavaElementContentProvider;

import org.eclipse.jdt.internal.ui.JavaPlugin;


public final class DestinationContentProvider extends StandardJavaElementContentProvider {
	
	private IReorgDestinationValidator fValidator;
	
	public DestinationContentProvider(IReorgDestinationValidator validator) {
		super(true);
		fValidator= validator;
	}
	
	public boolean hasChildren(Object element) {
		if (element instanceof IJavaElement){
			IJavaElement javaElement= (IJavaElement) element;
			if (! fValidator.canChildrenBeDestinations(javaElement))
				return false;
			if (javaElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT){
				if (((IPackageFragmentRoot)javaElement).isArchive())
					return false;
			}
		} else if (element instanceof IResource) {
			IResource resource= (IResource) element;
			if (! fValidator.canChildrenBeDestinations(resource))
				return false;
		}
		return super.hasChildren(element);
	}
	
	public Object[] getChildren(Object element) {
		try {
			if (element instanceof IJavaModel) {
				return concatenate(getJavaProjects((IJavaModel)element), getOpenNonJavaProjects((IJavaModel)element));
			} else {
				Object[] children= doGetChildren(element);
				ArrayList result= new ArrayList(children.length);
				for (int i= 0; i < children.length; i++) {
					if (children[i] instanceof IJavaElement) {
						IJavaElement javaElement= (IJavaElement) children[i];
						if (fValidator.canElementBeDestination(javaElement) || fValidator.canChildrenBeDestinations(javaElement))
							result.add(javaElement);
					} else if (children[i] instanceof IResource) {
						IResource resource= (IResource) children[i];
						if (fValidator.canElementBeDestination(resource) || fValidator.canChildrenBeDestinations(resource))
							result.add(resource);
					}
				}
				return result.toArray();
			}
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
			return new Object[0];
		}
	}

	private Object[] doGetChildren(Object parentElement) {
		if (parentElement instanceof IContainer) {
			final IContainer container= (IContainer) parentElement;
			return getResources(container);
		}
		return super.getChildren(parentElement);
	}
	
	// Copied from supertype
	private Object[] getResources(IContainer container) {
		try {
			IResource[] members= container.members();
			IJavaProject javaProject= JavaCore.create(container.getProject());
			if (javaProject == null || !javaProject.exists())
				return members;
			boolean isFolderOnClasspath = javaProject.isOnClasspath(container);
			List nonJavaResources= new ArrayList();
			// Can be on classpath but as a member of non-java resource folder
			for (int i= 0; i < members.length; i++) {
				IResource member= members[i];
				// A resource can also be a java element
				// in the case of exclusion and inclusion filters.
				// We therefore exclude Java elements from the list
				// of non-Java resources.
				if (isFolderOnClasspath) {
					if (javaProject.findPackageFragmentRoot(member.getFullPath()) == null) {
						nonJavaResources.add(member);
					} 
				} else if (!javaProject.isOnClasspath(member)) {
					nonJavaResources.add(member);
				}
			}
			return nonJavaResources.toArray();
		} catch(CoreException e) {
			return NO_CHILDREN;
		}
	}
	
	private static Object[] getOpenNonJavaProjects(IJavaModel model) throws JavaModelException {
		Object[] nonJavaProjects= model.getNonJavaResources();
		ArrayList result= new ArrayList(nonJavaProjects.length);
		for (int i= 0; i < nonJavaProjects.length; i++) {
			IProject project = (IProject) nonJavaProjects[i];
			if (project.isOpen())
				result.add(project);
		}
		return result.toArray();
	}

}
