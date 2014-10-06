/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.xml;

import static edu.buffalo.cse.green.constants.XMLConstants.XML_TAB;

import java.util.Map;
import java.util.Stack;

import edu.buffalo.cse.green.editor.DiagramEditor;

/**
 * Creates XML code and decodes XML strings.
 * 
 * @author bcmartin
 */
public class XMLConverter {
	private StringBuffer _buf = new StringBuffer();

	private Stack<XMLNode> _headers = new Stack<XMLNode>();

//	private String _projectName;

	private DiagramEditor _editor;

//	public XMLConverter() {
//		_projectName = null;
//	}

//	public XMLConverter(String currentProjectName) {
//		_projectName = currentProjectName;
//	}

	public XMLConverter(DiagramEditor editor) {
		_editor = editor;
	}
	/**
	 * @param xml - The contents of the XML file.
	 * @return The node representation of the XML.
	 * @throws ArrayIndexOutOfBoundsException
	 * @author bcmartin
	 * @author zgwang
	 */
	public XMLNode getDecodedXML(String xml)
			throws ArrayIndexOutOfBoundsException {
		int pos;
		String line;
		XMLNode node = new XMLNode("!root");
		XMLNode currentNode = node;

		while (true) {
			pos = xml.indexOf('>');
			if (pos == -1) break;
			pos++; //Account for position of line break character (/n)
		
			line = xml.substring(0, pos);
			xml = xml.substring(pos + 1);
			
			//Trims off remaining /n before tags
			while(xml.length() > 0 && xml.charAt(0) == '\n')
				xml = xml.substring(1);
			
			if (line.length() < 3) continue;

			currentNode = addLineToNode(currentNode, line);
		}

		return node;
	}

	/**
	 * @return The encoded XML.
	 */
	public String getEncodedXML() {
		_headers.clear();
		return _buf.toString();
	}

	/**
	 * Adds a header to the stack.
	 * 
	 * @param header - The node to add.
	 */
	public void pushHeader(XMLNode header) {
		
		//System.out.println("Header name to be added is " + header);
		
		// Original code -- 2 lines
		appendToBuffer("<" + header + ">");
		_headers.push(header);
		
	}

	/**
	 * Adds a header to the stack. A node with the given name will be created
	 * and added to the stack.
	 * 
	 * @param header - The name of the node to create and add.
	 */
	public void pushHeader(String header) {
		pushHeader(new XMLNode(header));
	}

	/**
	 * Creates an open header
	 * @param header - The name of the header.
	 * @param value - The value to assign to the header.
	 */
	public void openHeader(String header, String value) {
		
		//System.out.println("Header name is " + header + " with value " + value);
		appendToBuffer("<" + header + "=\"" + value + "\">");
		_headers.push(new XMLNode(header));
	}

	/**
	 * Removes a header from the stack.
	 */
	public void popHeader() {
		appendToBuffer("</" + _headers.pop() + ">");
	}

	/**
	 * Writes a closed XML header with the specified name and value.
	 * 
	 * @param header - The name.
	 * @param value - The value.
	 */
	public void writeKey(String header, Object value) {
		appendToBuffer("<" + header + "=\"" + value + "\"/>");
	}

	/**
	 * Writes all key-value pairs in the given map as closed XML headers.
	 * 
	 * @param map - The map.
	 */
	public void writeMap(Map<String, String> map) {
		for (String key : map.keySet()) {
			writeKey(key, map.get(key).toString());
		}
	}

	/**
	 * Writes the given string to the buffer, appending tabs as appropriate.
	 * 
	 * @param appendString - The string to append.
	 */
	private void appendToBuffer(String appendString) {
		String relativeFolderName = ".";

		for (int x = 0; x < _headers.size(); x++) {
			_buf.append(XML_TAB);
		}

		int tagStartPosition = appendString.indexOf("<")+1;
		int tagEndPosition = appendString.indexOf("=");
		if (!(appendString.indexOf("=") < 0)) {
			String tagName = appendString.substring(tagStartPosition, tagEndPosition);
			if (tagName.equals("name") || tagName.equals("sourceType") || tagName.equals("targetType")) {
				//			System.out.println(appendString);
				int projectNameStartPosition = appendString.indexOf("=\"=")+3;
				int projectNameEndPosition = appendString.indexOf("/");

				String projectNameInXML = appendString.substring(projectNameStartPosition,projectNameEndPosition);

				// If resource comes from same project, make name relative
				if (projectNameInXML.equals(getProjectName())) {
					int slashPos = appendString.indexOf("/");
					String restOfLine = appendString.substring(slashPos, appendString.length());
					String newString = "<" + tagName + "=\"=" + relativeFolderName + restOfLine;
					appendString = newString;
				}
				// otherwise just leave it alone
			}
		}
		_buf.append(appendString + "\n");
	}
	
	private String getProjectName() {
//		return _editor.getProject().getProject().getName();
		return ""; // TODO: remove
	}

	/**
	 * Adds the given line of text to the current node, manipulating headers as
	 * appropriate.
	 * 
	 * @param node - The node.
	 * @param line - The text.
	 * @return - The new node.
	 */
	private XMLNode addLineToNode(XMLNode node, String line) {
		
		//System.out.println("Adding node " + node + " with line " + line);
		
		line = line.substring(line.indexOf("<")).trim();
		
		if (line.substring(0, 2).equals("</")) {
			// close header
			return _headers.pop();
		} else {
	
			int len = line.length();
//System.out.println(">>> Line is >"+line+"<");
			if (line.substring(len - 2, len).equals("/>")) {
				// write value to map
				int pos = line.indexOf("=");
				String key = line.substring(1, pos);
				String val = line.substring(pos + 2, len - 3);
				node.getAttributes().put(key, val);
				return node;
			} else {
				// open header
				pushHeader(node);
				String name = line.substring(1, len - 1);
				XMLNode newNode = new XMLNode(name);
				node.addChild(newNode);
				return newNode;
			}
		}
	}
}