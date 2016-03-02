/**
 * © Nowina Solutions, 2015-2015
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

/**
 * This utility class allows to execute a native process and
 * gets its standard output.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class NativeProcessExecutor {

	private final String command;
	private final long timeoutInMillis;
	private boolean executed;
	private String result;
	private int resultCode;
	
	public NativeProcessExecutor(final String command, final int timeoutInMillis) {
		this.command = command;
		this.timeoutInMillis = timeoutInMillis;
		this.executed = false;
		this.result = null;
		this.resultCode = -1;
	}

	public String getResult() {
		if(!executed) {
			execute();
		}
		if(resultCode != 0) {
			throw new IllegalStateException("Result code is " + resultCode);
		} else {
			return result;
		}
	}
	
	public int getResultCode() {
		if(!executed) {
			execute();
		}
		return resultCode;
	}
	
	private void execute() {
		try {
			final Process process = Runtime.getRuntime().exec(command);
			final FutureTask<String> futureTask = new FutureTask<String>(new StreamReader(process.getInputStream()));
			final Thread thread = new Thread(futureTask);
			thread.start();
			if(!process.waitFor(timeoutInMillis, TimeUnit.MILLISECONDS)) {
				throw new RuntimeException("Timeout when executing command: " + command);
			}
			resultCode = process.exitValue();

			if(resultCode == 0) {
				result = futureTask.get();
			}
			
			executed = true;
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new RuntimeException("An exception occurred when executing " + command, e);
		}
	}

	private static class StreamReader implements Callable<String> {
		private InputStream is;
		private StringWriter writer;
		
		public StreamReader(InputStream is) {
			this.is = is;
			writer = new StringWriter();
		}
		
		@Override
		public String call() {
			try {
				IOUtils.copy(is, writer);
				return writer.toString();
			} catch (IOException e) {
				throw new RuntimeException("Unable to read InputStream", e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
}
