package org.motechproject.config.service;

import org.motechproject.config.domain.BootstrapConfig;

/**
 * <p>Central configuration service that monitors and manages configurations.</p>
 */
public interface ConfigurationService {

    /**
     * <p>Loads bootstrap config that is used to start up the Motech server.</p>
     * <p>
     *     The bootstrap configuration is loaded in the following order:
     *     <ol>
     *         <li>
     *             Load the configuration from <code>bootstrap.properties</code> from the config
     *             directory specified by the environment variable <code>MOTECH_CONFIG_DIR</code>.
     *             <code>bootstrap.properties</code> contains the following properties:
     *             <pre>
     *                 db.url (Mandatory)
     *                 db.username (If required)
     *                 db.password (If required)
     *                 tenant.id (Optional. Defaults to 'DEFAULT')
     *                 config.source (Optional. Defaults to 'UI')
     *             </pre>
     *             An example <code>bootstrap.properties</code> is given below:
     *             <pre>
     *                 db.url=http://localhost:5984
     *                 db.username=motech
     *                 db.password=motech
     *                 tenant.id=MotherChildCare
     *                 config.source=FILE
     *             </pre>
     *         </li>
     *         <li>
     *             If <code>MOTECH_CONFIG_DIR</code> environment variable is <b>not</b> set, load the specific
     *             configuration values from the following environment variables:
     *             <pre>
     *                  MOTECH_DB_URL (Mandatory)
     *                  MOTECH_DB_USERNAME (If required)
     *                  MOTECH_DB_PASSWORD (If required)
     *                  MOTECH_TENANT_ID (Optional. Defaults to 'DEFAULT')
     *                  MOTECH_CONFIG_SOURCE (Optional. Defaults to 'UI')
     *             </pre>
     *         </li>
     *         <li>
     *             If <code>MOTECH_DB_URL</code> environment is not set, load the configuration from
     *             <code>bootstrap.properties</code> from the default Motech config directory specified by the
     *             property <code>default.bootstrap.config.dir</code> in the file <code>config.properties</code>.
     *             <code>config.properties</code> should be present in the root of classpath. A <code>config.properties</code>
     *             is provided in the bundle jar with <code>default.bootstrap.config.dir</code> set to
     *             <code>/etc/motech/config</code>. User may override this value by providing another
     *             <code>config.properties</code>.
     *         </li>
     *     </ol>
     * </p>
     * @return Bootstrap configuration
     * @throws org.motechproject.config.MotechConfigurationException if bootstrap configuration cannot be loaded.
     */
    BootstrapConfig loadBootstrapConfig();
}
