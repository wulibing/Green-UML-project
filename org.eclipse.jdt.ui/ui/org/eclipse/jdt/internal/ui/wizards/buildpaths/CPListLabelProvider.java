/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.wizards.buildpaths;

import org.eclipse.core.runtime.IPath;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.ui.IWorkbench;

import org.eclipse.ui.ide.IDE;

import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration;
import org.eclipse.jdt.ui.wizards.ClasspathAttributeConfiguration.ClasspathAttributeAccess;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;

public class CPListLabelProvider extends LabelProvider {
		
	private String fNewLabel, fClassLabel, fCreateLabel;
		
	private ImageDescriptorRegistry fRegistry;
	private ISharedImages fSharedImages;

	private ImageDescriptor fProjectImage;
	
	private ClasspathAttributeConfigurationDescriptors fAttributeDescriptors;
	
	
	public CPListLabelProvider() {
		fNewLabel= NewWizardMessages.CPListLabelProvider_new; 
		fClassLabel= NewWizardMessages.CPListLabelProvider_classcontainer; 
		fCreateLabel= NewWizardMessages.CPListLabelProvider_willbecreated; 
		fRegistry= JavaPlugin.getImageDescriptorRegistry();
	
		fSharedImages= JavaUI.getSharedImages();

		IWorkbench workbench= JavaPlugin.getDefault().getWorkbench();
		
		fProjectImage= workbench.getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
		fAttributeDescriptors= JavaPlugin.getDefault().getClasspathAttributeConfigurationDescriptors();
	}
	
	public String getText(Object element) {
		if (element instanceof CPListElement) {
			return getCPListElementText((CPListElement) element);
		} else if (element instanceof CPListElementAttribute) {
			CPListElementAttribute attribute= (CPListElementAttribute) element;
			String text= getCPListElementAttributeText(attribute);
			if (attribute.isNonModifiable()) {
				return Messages.format(NewWizardMessages.CPListLabelProvider_non_modifiable_attribute, text); 
			}
			return text;
		} else if (element instanceof CPUserLibraryElement) {
			return getCPUserLibraryText((CPUserLibraryElement) element);
		} else if (element instanceof IAccessRule) {
			IAccessRule rule= (IAccessRule) element;
			return Messages.format(NewWizardMessages.CPListLabelProvider_access_rules_label, new String[] { AccessRulesLabelProvider.getResolutionLabel(rule.getKind()), rule.getPattern().toString()}); 
		}
		return super.getText(element);
	}
	
	public String getCPUserLibraryText(CPUserLibraryElement element) {
		String name= element.getName();
		if (element.isSystemLibrary()) {
			name= Messages.format(NewWizardMessages.CPListLabelProvider_systemlibrary, name); 
		}
		return name;
	}

	public String getCPListElementAttributeText(CPListElementAttribute attrib) {
		String notAvailable= NewWizardMessages.CPListLabelProvider_none; 
		String key= attrib.getKey();
		if (key.equals(CPListElement.SOURCEATTACHMENT)) {
			String arg;
			IPath path= (IPath) attrib.getValue();
			if (path != null && !path.isEmpty()) {
				if (attrib.getParent().getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
					arg= getVariableString(path);
				} else {
					arg= getPathString(path, path.getDevice() != null);
				}
			} else {
				arg= notAvailable;
			}
			return Messages.format(NewWizardMessages.CPListLabelProvider_source_attachment_label, new String[] { arg }); 
		} else if (key.equals(CPListElement.OUTPUT)) {
			String arg= null;
			IPath path= (IPath) attrib.getValue();
			if (path != null) {
				arg= path.makeRelative().toString();
			} else {
				arg= NewWizardMessages.CPListLabelProvider_default_output_folder_label; 
			}
			return Messages.format(NewWizardMessages.CPListLabelProvider_output_folder_label, new String[] { arg }); 
		} else if (key.equals(CPListElement.EXCLUSION)) {
			String arg= null;
			IPath[] patterns= (IPath[]) attrib.getValue();
			if (patterns != null && patterns.length > 0) {
				int patternsCount= 0;
				StringBuffer buf= new StringBuffer();
				for (int i= 0; i < patterns.length; i++) {
					String pattern= patterns[i].toString();
					if (pattern.length() > 0) {
						if (patternsCount > 0) {
							buf.append(NewWizardMessages.CPListLabelProvider_exclusion_filter_separator); 
						}
						buf.append(pattern);
						patternsCount++;
					}
				}
				if (patternsCount > 0) {
					arg= buf.toString();
				} else {
					arg= notAvailable;
				}
			} else {
				arg= notAvailable;
			}
			return Messages.format(NewWizardMessages.CPListLabelProvider_exclusion_filter_label, new String[] { arg }); 
		} else if (key.equals(CPListElement.INCLUSION)) {
			String arg= null;
			IPath[] patterns= (IPath[]) attrib.getValue();
			if (patterns != null && patterns.length > 0) {
				int patternsCount= 0;
				StringBuffer buf= new StringBuffer();
				for (int i= 0; i < patterns.length; i++) {
					String pattern= patterns[i].toString();
					if (pattern.length() > 0) {
						if (patternsCount > 0) {
							buf.append(NewWizardMessages.CPListLabelProvider_inclusion_filter_separator);
						}
						buf.append(pattern);
						patternsCount++;
					}					
				}
				if (patternsCount > 0) {
					arg= buf.toString();
				} else {
					arg= notAvailable;
				}
			} else {
				arg= NewWizardMessages.CPListLabelProvider_all; 
			}
			return Messages.format(NewWizardMessages.CPListLabelProvider_inclusion_filter_label, new String[] { arg });
		} else if (key.equals(CPListElement.ACCESSRULES)) {
			IAccessRule[] rules= (IAccessRule[]) attrib.getValue();
			int nRules= rules != null ? rules.length : 0;
			
			int parentKind= attrib.getParent().getEntryKind();
			if (parentKind == IClasspathEntry.CPE_PROJECT) {
				Boolean combined= (Boolean) attrib.getParent().getAttribute(CPListElement.COMBINE_ACCESSRULES);
				if (nRules > 0) {
					if (combined.booleanValue()) {
						return Messages.format(NewWizardMessages.CPListLabelProvider_project_access_rules_combined, String.valueOf(nRules)); 
					} else {
						return Messages.format(NewWizardMessages.CPListLabelProvider_project_access_rules_not_combined, String.valueOf(nRules)); 
					}
				} else {
					return NewWizardMessages.CPListLabelProvider_project_access_rules_no_rules; 
				}
			} else if (parentKind == IClasspathEntry.CPE_CONTAINER) {
				if (nRules > 0) {
					return Messages.format(NewWizardMessages.CPListLabelProvider_container_access_rules, String.valueOf(nRules)); 
				} else {
					return NewWizardMessages.CPListLabelProvider_container_no_access_rules; 
				}
			} else {
				if (nRules > 0) {
					return Messages.format(NewWizardMessages.CPListLabelProvider_access_rules_enabled, String.valueOf(nRules)); 
				} else {
					return NewWizardMessages.CPListLabelProvider_access_rules_disabled; 
				}
			}
		} else {
			ClasspathAttributeConfiguration config= fAttributeDescriptors.get(key);
			if (config != null) {
				ClasspathAttributeAccess access= attrib.getClasspathAttributeAccess();
				String nameLabel= config.getNameLabel(access);
				String valueLabel= config.getValueLabel(access);
				return Messages.format(NewWizardMessages.CPListLabelProvider_attribute_label, new String[] { nameLabel, valueLabel }); 
			}
			String arg= (String) attrib.getValue();
			if (arg == null) {
				arg= notAvailable; 
			}
			return Messages.format(NewWizardMessages.CPListLabelProvider_attribute_label, new String[] { key, arg }); 
		}
	}
	
	public String getCPListElementText(CPListElement cpentry) {
		IPath path= cpentry.getPath();
		switch (cpentry.getEntryKind()) {
			case IClasspathEntry.CPE_LIBRARY: {
				IResource resource= cpentry.getResource();
				if (resource instanceof IContainer) {
					StringBuffer buf= new StringBuffer(path.makeRelative().toString());
					IPath linkTarget= cpentry.getLinkTarget();
					if (linkTarget != null) {
						buf.append(JavaElementLabels.CONCAT_STRING);
						buf.append(linkTarget.toOSString());
					}
					buf.append(' ');
					buf.append(fClassLabel);
					if (!resource.exists()) {
						buf.append(' ');
						if (cpentry.isMissing()) {
							buf.append(fCreateLabel);
						} else {
							buf.append(fNewLabel);
						}
					}
					return buf.toString();
				} else if (ArchiveFileFilter.isArchivePath(path)) {
					return getPathString(path, resource == null);
				}
				// should not get here
				return path.makeRelative().toString();
			}
			case IClasspathEntry.CPE_VARIABLE: {
				return getVariableString(path);
			}
			case IClasspathEntry.CPE_PROJECT:
				return path.lastSegment();
			case IClasspathEntry.CPE_CONTAINER:
				try {
					IClasspathContainer container= JavaCore.getClasspathContainer(path, cpentry.getJavaProject());
					if (container != null) {
						return container.getDescription();
					}
					ClasspathContainerInitializer initializer= JavaCore.getClasspathContainerInitializer(path.segment(0));
					if (initializer != null) {
						String description= initializer.getDescription(path, cpentry.getJavaProject());
						return Messages.format(NewWizardMessages.CPListLabelProvider_unbound_library, description); 
					}
				} catch (JavaModelException e) {
	
				}
				return path.toString();
			case IClasspathEntry.CPE_SOURCE: {
				StringBuffer buf= new StringBuffer(path.makeRelative().toString());
				IPath linkTarget= cpentry.getLinkTarget();
				if (linkTarget != null) {
					buf.append(JavaElementLabels.CONCAT_STRING);
					buf.append(linkTarget.toOSString());
				}
				IResource resource= cpentry.getResource();
				if (resource != null && !resource.exists()) {
					buf.append(' ');
					if (cpentry.isMissing()) {
						buf.append(fCreateLabel);
					} else {
						buf.append(fNewLabel);
					}
				} else if (cpentry.getOrginalPath() == null) {
					buf.append(' ');
					buf.append(fNewLabel);
				}
				return buf.toString();
			}
			default:
				// pass
		}
		return NewWizardMessages.CPListLabelProvider_unknown_element_label; 
	}
	
	private String getPathString(IPath path, boolean isExternal) {
		if (ArchiveFileFilter.isArchivePath(path)) {
			IPath appendedPath= path.removeLastSegments(1);
			String appended= isExternal ? appendedPath.toOSString() : appendedPath.makeRelative().toString();
			return Messages.format(NewWizardMessages.CPListLabelProvider_twopart, new String[] { path.lastSegment(), appended }); 
		} else {
			return isExternal ? path.toOSString() : path.makeRelative().toString();
		}
	}
	
	private String getVariableString(IPath path) {
		String name= path.makeRelative().toString();
		IPath entryPath= JavaCore.getClasspathVariable(path.segment(0));
		if (entryPath != null) {
			String appended= entryPath.append(path.removeFirstSegments(1)).toOSString();
			return Messages.format(NewWizardMessages.CPListLabelProvider_twopart, new String[] { name, appended }); 
		} else {
			return name;
		}
	}
	
	private ImageDescriptor getCPListElementBaseImage(CPListElement cpentry) {
		switch (cpentry.getEntryKind()) {
			case IClasspathEntry.CPE_SOURCE:
				if (cpentry.getPath().segmentCount() == 1) {
					return fProjectImage;
				} else {
					return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_PACKFRAG_ROOT);
				}
			case IClasspathEntry.CPE_LIBRARY:
				IResource res= cpentry.getResource();
				IPath path= (IPath) cpentry.getAttribute(CPListElement.SOURCEATTACHMENT);
				if (res == null) {
					if (path == null || path.isEmpty()) {
						return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_EXTERNAL_ARCHIVE);
					} else {
						return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_EXTERNAL_ARCHIVE_WITH_SOURCE);
					}
				} else if (res instanceof IFile) {
					if (path == null || path.isEmpty()) {
						return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_JAR);
					} else {
						return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_JAR_WITH_SOURCE);
					}
				} else {
					return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_PACKFRAG_ROOT);
				}
			case IClasspathEntry.CPE_PROJECT:
				return fProjectImage;
			case IClasspathEntry.CPE_VARIABLE:
				ImageDescriptor variableImage= fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_CLASSPATH_VAR_ENTRY);
				if (cpentry.isDeprecated()) {
					return new JavaElementImageDescriptor(variableImage, JavaElementImageDescriptor.DEPRECATED, JavaElementImageProvider.SMALL_SIZE);
				}
				return variableImage;
			case IClasspathEntry.CPE_CONTAINER:
				return fSharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_LIBRARY);
			default:
				return null;
		}
	}			
		
	public Image getImage(Object element) {
		if (element instanceof CPListElement) {
			CPListElement cpentry= (CPListElement) element;
			ImageDescriptor imageDescriptor= getCPListElementBaseImage(cpentry);
			if (imageDescriptor != null) {
				if (cpentry.isMissing()) {
					imageDescriptor= new JavaElementImageDescriptor(imageDescriptor, JavaElementImageDescriptor.WARNING, JavaElementImageProvider.SMALL_SIZE);
				}
				return fRegistry.get(imageDescriptor);
			}
		} else if (element instanceof CPListElementAttribute) {
			CPListElementAttribute attribute= (CPListElementAttribute) element;
			String key= (attribute).getKey();
			if (key.equals(CPListElement.SOURCEATTACHMENT)) {
				return fRegistry.get(JavaPluginImages.DESC_OBJS_SOURCE_ATTACH_ATTRIB);
			} else if (key.equals(CPListElement.OUTPUT)) {
				return fRegistry.get(JavaPluginImages.DESC_OBJS_OUTPUT_FOLDER_ATTRIB);
			} else if (key.equals(CPListElement.EXCLUSION)) {
				return fRegistry.get(JavaPluginImages.DESC_OBJS_EXCLUSION_FILTER_ATTRIB);
			} else if (key.equals(CPListElement.INCLUSION)) {
				return fRegistry.get(JavaPluginImages.DESC_OBJS_INCLUSION_FILTER_ATTRIB);
			} else if (key.equals(CPListElement.ACCESSRULES)) {
				return fRegistry.get(JavaPluginImages.DESC_OBJS_ACCESSRULES_ATTRIB);
			} else {
				ClasspathAttributeConfiguration config= fAttributeDescriptors.get(key);
				if (config != null) {
					return fRegistry.get(config.getImageDescriptor(attribute.getClasspathAttributeAccess()));
				}
			}
			return  fSharedImages.getImage(ISharedImages.IMG_OBJS_CLASSPATH_VAR_ENTRY);
		} else if (element instanceof CPUserLibraryElement) {
			return  fSharedImages.getImage(ISharedImages.IMG_OBJS_LIBRARY);
		} else if (element instanceof IAccessRule) {
			IAccessRule rule= (IAccessRule) element;
			return AccessRulesLabelProvider.getResolutionImage(rule.getKind());
		}
		return null;
	}


}	
