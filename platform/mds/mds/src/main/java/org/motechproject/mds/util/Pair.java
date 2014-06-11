package org.motechproject.mds.util;

/**
 * The <code>Pair</code> util interface should use everywhere where developer needs a pair of
 * key-value
 *
 * @param <N> type of key
 * @param <V> type of value
 * @see org.motechproject.mds.domain.FieldMetadata
 * @see org.motechproject.mds.domain.FieldSetting
 * @see org.motechproject.mds.dto.MetadataDto
 * @see org.motechproject.mds.dto.SettingDto
 */
public interface Pair<N, V> {

    N getKey();

    V getValue();
}
