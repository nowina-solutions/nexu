/**
 * © Nowina Solutions, 2015-2016
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
package lu.nowina.nexu;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.flow.OperationFactory;
import lu.nowina.nexu.api.plugin.HttpPlugin;
import lu.nowina.nexu.api.plugin.InitializationMessage;
import lu.nowina.nexu.api.plugin.NexuPlugin;
import lu.nowina.nexu.flow.FlowRegistry;
import lu.nowina.nexu.generic.SCDatabase;
import lu.nowina.nexu.view.core.UIDisplay;

/**
 * Builds an instance of {@link NexuAPI}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class APIBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(APIBuilder.class.getName());
	
	public APIBuilder() {
		super();
	}

	/**
	 * Builds and returns an instance of {@link NexuAPI}.
	 * @param display The implementation of {@link UIDisplay} used to display UI elements.
	 * @param appConfig The configuration parameters.
	 * @param flowRegistry The implementation of {@link FlowRegistry} to use.
	 * @param localDatabase The local database of smartcards.
	 * @param remoteDatabaseLoader The loader of remote database of smartcards (can be <code>null</code> if none).
	 * @param operationFactory The implementation of {@link OperationFactory} to use.
	 * @return The built instance of {@link NexuAPI}.
	 */
	public NexuAPI build(final UIDisplay display, final AppConfig appConfig, final FlowRegistry flowRegistry,
			final SCDatabase localDatabase, final OperationFactory operationFactory) {
		final CardDetector detector = new CardDetector(EnvironmentInfo.buildFromSystemProperties(System.getProperties()));

		return new InternalAPI(display, localDatabase, detector, flowRegistry,
				operationFactory, appConfig);
	}

	/**
	 * Init plugins on the given {@link NexuAPI} instance.
	 * @param api The {@link NexuAPI} instance on which plugins must be initialized. It <strong>MUST</strong> be
	 * an instance previously returned by {@link APIBuilder#build(UIDisplay, AppConfig, FlowRegistry, SCDatabase, ProductDatabaseRefresher, OperationFactory)}.
	 * @param properties Configuration properties of the plugin to initialize.
	 * @return Messages about events that occurred during plugins initialization.
	 */
	public List<InitializationMessage> initPlugins(final NexuAPI api, final Properties properties) {
		final List<InitializationMessage> messages = new ArrayList<>();
		for (final String key : properties.stringPropertyNames()) {
			if (key.startsWith("plugin_")) {
				final String pluginClassName = properties.getProperty(key);
				final String pluginId = key.substring("plugin_".length());

				LOGGER.info(" + Plugin " + pluginClassName);
				messages.addAll(buildAndRegisterPlugin((InternalAPI) api, pluginClassName, pluginId));
			}
		}
		return messages;
	}
	
	private List<InitializationMessage> buildAndRegisterPlugin(InternalAPI api, String pluginClassName, String pluginId) {
		try {
			final Class<? extends NexuPlugin> clazz = Class.forName(pluginClassName).asSubclass(NexuPlugin.class);
			final NexuPlugin plugin = clazz.newInstance();
			final List<InitializationMessage> messages = plugin.init(pluginId, api);
			for (Object o : ClassUtils.getAllInterfaces(clazz)) {
				registerPlugin(api, pluginId, (Class<?>) o, plugin);
			}
			return messages;
		} catch (final Exception e) {
			LOGGER.error(MessageFormat.format("Cannot register plugin {0} (id: {1})", pluginClassName, pluginId), e);
			throw new NexuException(e);
		}
	}

	private void registerPlugin(InternalAPI api, String pluginId, Class<?> i, Object plugin) {
		if (HttpPlugin.class.equals(i)) {
			final HttpPlugin p = (HttpPlugin) plugin;
			api.registerHttpContext(pluginId, p);
		}
	}
}
