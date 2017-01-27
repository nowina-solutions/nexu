/**
 * © Nowina Solutions, 2015-2017
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
package lu.nowina.nexu.systray;

import java.net.URL;

import lu.nowina.nexu.SystrayMenu;
import lu.nowina.nexu.api.SystrayMenuItem;
import lu.nowina.nexu.api.flow.OperationFactory;

/**
 * Interface for {@link SystrayMenu} initializers.
 * 
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public interface SystrayMenuInitializer {

    /**
     * Initializes the systray menu.
     * @param tooltip The tooltip to show (if supported).
     * @param trayIconURL The URL for the tray icon.
     * @param operationFactory The {@link OperationFactory}.
     * @param exitMenuItem The systray menu item for exit.
     * @param systrayMenuItems Systray menu items.
     */
    void init(final String tooltip, final URL trayIconURL, final OperationFactory operationFactory,
    		final SystrayMenuItem exitMenuItem, final SystrayMenuItem... systrayMenuItems);
    
}
