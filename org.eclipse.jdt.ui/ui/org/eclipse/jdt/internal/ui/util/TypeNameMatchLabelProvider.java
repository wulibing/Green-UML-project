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
package org.eclipse.jdt.internal.ui.util;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.search.TypeNameMatch;

import org.eclipse.jdt.ui.JavaElementLabels;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.JavaUIMessages;

public class TypeNameMatchLabelProvider extends LabelProvider {
	
	public static final int SHOW_FULLYQUALIFIED=		0x01;
	public static final int SHOW_PACKAGE_POSTFIX=		0x02;
	public static final int SHOW_PACKAGE_ONLY=			0x04;
	public static final int SHOW_ROOT_POSTFIX=			0x08;
	public static final int SHOW_TYPE_ONLY=				0x10;
	public static final int SHOW_TYPE_CONTAINER_ONLY=	0x20;
	public static final int SHOW_POST_QUALIFIED=		0x40;
	
	private static final Image CLASS_ICON= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS);
	private static final Image ANNOTATION_ICON= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_ANNOTATION);
	private static final Image INTERFACE_ICON= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_INTERFACE);
	private static final Image ENUM_ICON= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_ENUM);
	private static final Image PKG_ICON= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_PACKAGE);
	
	private int fFlags;
	
	public TypeNameMatchLabelProvider(int flags) {
		fFlags= flags;
	}	
	
	private boolean isSet(int flag) {
		return (fFlags & flag) != 0;
	}

	private String getPackageName(String packName) {
		if (packName.length() == 0)
			return JavaUIMessages.TypeInfoLabelProvider_default_package; 
		else
			return packName;
	}

	/* non java-doc
	 * @see ILabelProvider#getText
	 */
	public String getText(Object element) {
		if (! (element instanceof TypeNameMatch)) 
			return super.getText(element);
		
		TypeNameMatch typeRef= (TypeNameMatch) element;
		StringBuffer buf= new StringBuffer();
		if (isSet(SHOW_TYPE_ONLY)) {
			buf.append(typeRef.getSimpleTypeName());
		} else if (isSet(SHOW_TYPE_CONTAINER_ONLY)) {
			String containerName= typeRef.getTypeContainerName();
			buf.append(getPackageName(containerName));
		} else if (isSet(SHOW_PACKAGE_ONLY)) {
			String packName= typeRef.getPackageName();
			buf.append(getPackageName(packName));
		} else {
			if (isSet(SHOW_FULLYQUALIFIED)) {
				buf.append(typeRef.getFullyQualifiedName());
			} else if (isSet(SHOW_POST_QUALIFIED)) {
				buf.append(typeRef.getSimpleTypeName());
				String containerName= typeRef.getTypeContainerName();
				if (containerName != null && containerName.length() > 0) {
					buf.append(JavaElementLabels.CONCAT_STRING);
					buf.append(containerName);
				}
			} else {
				buf.append(typeRef.getTypeQualifiedName());
			}

			if (isSet(SHOW_PACKAGE_POSTFIX)) {
				buf.append(JavaElementLabels.CONCAT_STRING);
				String packName= typeRef.getPackageName();
				buf.append(getPackageName(packName));
			}
		}
		if (isSet(SHOW_ROOT_POSTFIX)) {
			buf.append(JavaElementLabels.CONCAT_STRING);
			IPackageFragmentRoot root= typeRef.getPackageFragmentRoot();
			JavaElementLabels.getPackageFragmentRootLabel(root, JavaElementLabels.ROOT_QUALIFIED, buf);
		}
		return buf.toString();				
	}
	
	/* non java-doc
	 * @see ILabelProvider#getImage
	 */	
	public Image getImage(Object element) {
		if (! (element instanceof TypeNameMatch)) 
			return super.getImage(element);	

		if (isSet(SHOW_TYPE_CONTAINER_ONLY)) {
			TypeNameMatch typeRef= (TypeNameMatch) element;
			if (typeRef.getPackageName().equals(typeRef.getTypeContainerName()))
				return PKG_ICON;

			// XXX cannot check outer type for interface efficiently (5887)
			return CLASS_ICON;

		} else if (isSet(SHOW_PACKAGE_ONLY)) {
			return PKG_ICON;
		} else {
			int modifiers= ((TypeNameMatch)element).getModifiers();
			if (Flags.isAnnotation(modifiers)) {
				return ANNOTATION_ICON;
			} else if (Flags.isEnum(modifiers)) {
				return ENUM_ICON;
			} else if (Flags.isInterface(modifiers)) {
				return INTERFACE_ICON;
			}
			return CLASS_ICON;
		}
	}	
}
