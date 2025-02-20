// Generated from D:/Projects/Nyx/nyx/src/main/antlr4/com/nyx/shell/Shell.g4 by ANTLR 4.13.2

package com.nyx.shell;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link ShellVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
@SuppressWarnings("CheckReturnValue")
public class ShellBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements ShellVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitCommand(ShellParser.CommandContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitToken(ShellParser.TokenContext ctx) { return visitChildren(ctx); }
}