/*******************************************************************************
 * Copyright (c) 2009, 2014 Xored Software Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Xored Software Inc - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.rcptt.tesla.core.protocol;

import org.eclipse.rcptt.tesla.core.protocol.raw.Element;

public class LinkUIElement extends ControlUIElement {

	public LinkUIElement(Element e, UIPlayer player) {
		super(e, player);
	}

	public void clickLink(String ref) {
		ClickLink clickLink = factory.createClickLink();
		clickLink.setElement(getElement());
		clickLink.setRef(ref);
		player.safeExecuteCommand(clickLink);
	}

	/**
	 * Convenience method for {@link #parse(String)}
	 * @param string
	 * @return
	 */
	public static String firstRef(String string) {
		String[] result = parse(string);
		if(result.length == 0) {
			return null;
		}
		return result[0];
	}
	/**
	 * Parses link text and returns array of hrefs
	 * This method has been copied from Link class
	 *  
	 * @param string
	 * @return
	 */
	public static String[] parse(String string) {
		int length = string.length();
		int[] offsets = new int[length / 4];
		String[] ids = new String[length / 4];
		int[] mnemonics = new int[length / 4 + 1];
		StringBuffer result = new StringBuffer();
		char[] buffer = new char[length];
		string.getChars(0, string.length(), buffer, 0);
		int index = 0, state = 0, linkIndex = 0;
		int start = 0, tagStart = 0, linkStart = 0, endtagStart = 0, refStart = 0;
		while (index < length) {
			char c = Character.toLowerCase(buffer[index]);
			switch (state) {
			case 0:
				if (c == '<') {
					tagStart = index;
					state++;
				}
				break;
			case 1:
				if (c == 'a')
					state++;
				break;
			case 2:
				switch (c) {
				case 'h':
					state = 7;
					break;
				case '>':
					linkStart = index + 1;
					state++;
					break;
				default:
					if (Character.isWhitespace(c))
						break;
					else
						state = 13;
				}
				break;
			case 3:
				if (c == '<') {
					endtagStart = index;
					state++;
				}
				break;
			case 4:
				state = c == '/' ? state + 1 : 3;
				break;
			case 5:
				state = c == 'a' ? state + 1 : 3;
				break;
			case 6:
				if (c == '>') {
					mnemonics[linkIndex] = parseMnemonics(buffer, start,
							tagStart, result);
					int offset = result.length();
					parseMnemonics(buffer, linkStart, endtagStart, result);
					offsets[linkIndex] = offset;
					if (ids[linkIndex] == null) {
						ids[linkIndex] = new String(buffer, linkStart,
								endtagStart - linkStart);
					}
					linkIndex++;
					start = tagStart = linkStart = endtagStart = refStart = index + 1;
					state = 0;
				} else {
					state = 3;
				}
				break;
			case 7:
				state = c == 'r' ? state + 1 : 0;
				break;
			case 8:
				state = c == 'e' ? state + 1 : 0;
				break;
			case 9:
				state = c == 'f' ? state + 1 : 0;
				break;
			case 10:
				state = c == '=' ? state + 1 : 0;
				break;
			case 11:
				if (c == '"') {
					state++;
					refStart = index + 1;
				} else {
					state = 0;
				}
				break;
			case 12:
				if (c == '"') {
					ids[linkIndex] = new String(buffer, refStart, index
							- refStart);
					state = 2;
				}
				break;
			case 13:
				if (Character.isWhitespace(c)) {
					state = 0;
				} else if (c == '=') {
					state++;
				}
				break;
			case 14:
				state = c == '"' ? state + 1 : 0;
				break;
			case 15:
				if (c == '"')
					state = 2;
				break;
			default:
				state = 0;
				break;
			}
			index++;
		}
		if (start < length) {
			int tmp = parseMnemonics(buffer, start, tagStart, result);
			int mnemonic = parseMnemonics(buffer,
					Math.max(tagStart, linkStart), length, result);
			if (mnemonic == -1)
				mnemonic = tmp;
			mnemonics[linkIndex] = mnemonic;
		} else {
			mnemonics[linkIndex] = -1;
		}
		if (offsets.length != linkIndex) {
			int[] newOffsets = new int[linkIndex];
			System.arraycopy(offsets, 0, newOffsets, 0, linkIndex);
			offsets = newOffsets;
			String[] newIDs = new String[linkIndex];
			System.arraycopy(ids, 0, newIDs, 0, linkIndex);
			ids = newIDs;
			int[] newMnemonics = new int[linkIndex + 1];
			System.arraycopy(mnemonics, 0, newMnemonics, 0, linkIndex + 1);
			mnemonics = newMnemonics;
		}
		return ids;
	}

	private static int parseMnemonics(char[] buffer, int start, int end, StringBuffer result) {
		int mnemonic = -1, index = start;
		while (index < end) {
			if (buffer[index] == '&') {
				if (index + 1 < end && buffer[index + 1] == '&') {
					result.append(buffer[index]);
					index++;
				} else {
					mnemonic = result.length();
				}
			} else {
				result.append(buffer[index]);
			}
			index++;
		}
		return mnemonic;
	}
}
