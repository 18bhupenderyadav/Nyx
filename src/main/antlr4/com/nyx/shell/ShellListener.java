// Generated from D:/Projects/Nyx/nyx/src/main/antlr4/com/nyx/shell/Shell.g4 by ANTLR 4.13.2

package com.nyx.shell;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ShellParser}.
 */
public interface ShellListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ShellParser#command}.
	 * @param ctx the parse tree
	 */
	void enterCommand(ShellParser.CommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShellParser#command}.
	 * @param ctx the parse tree
	 */
	void exitCommand(ShellParser.CommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShellParser#token}.
	 * @param ctx the parse tree
	 */
	void enterToken(ShellParser.TokenContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShellParser#token}.
	 * @param ctx the parse tree
	 */
	void exitToken(ShellParser.TokenContext ctx);
}