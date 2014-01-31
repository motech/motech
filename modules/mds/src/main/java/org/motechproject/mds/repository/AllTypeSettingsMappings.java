package org.motechproject.mds.repository;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.SettingOptionsMapping;
import org.motechproject.mds.domain.TypeSettingsMapping;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.SettingOptions;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The <code>AllTypeSettingsMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.TypeSettingsMapping}.
 */
@Repository
public class AllTypeSettingsMappings extends BaseMdsRepository {

    public TypeSettingsMapping save(SettingDto setting, AvailableFieldTypeMapping valueType, AvailableFieldTypeMapping type) {
        TypeSettingsMapping typeSettingsMapping;
        List<SettingOptions> options = setting.getOptions();
        if (options != null) {
            List<SettingOptionsMapping>settingOptionsMappings = new ArrayList<>();
            for (SettingOptions option : options) {
                settingOptionsMappings.add(new SettingOptionsMapping(option));
            }

            typeSettingsMapping = new TypeSettingsMapping(setting.getName(), setting.getValue().toString(),
                    valueType, type, settingOptionsMappings.toArray(new SettingOptionsMapping[options.size()]));
        } else {
            typeSettingsMapping = new TypeSettingsMapping(setting.getName(), setting.getValue().toString(), valueType, type);
        }

        return save(typeSettingsMapping);
    }

    public TypeSettingsMapping save(TypeSettingsMapping typeSettings) {
        return getPersistenceManager().makePersistent(typeSettings);
    }

    public List<TypeSettingsMapping> createEmptySettingsInstance(AvailableFieldTypeMapping type) {
        List<TypeSettingsMapping> settingsForType = getSettingsForType(type);

        List<TypeSettingsMapping> newInstance = new ArrayList<>();
        for (TypeSettingsMapping typeSettings : settingsForType) {
            newInstance.add(typeSettings.copy());
        }

        return newInstance;
    }

    public List<TypeSettingsMapping> getSettingsForType(AvailableFieldTypeMapping type) {
        Query query = getPersistenceManager().newQuery(TypeSettingsMapping.class);
        query.setFilter("typeId == type && field == null");
        query.declareParameters("java.lang.Long typeId");

        Collection collection = (Collection) query.execute(type.getId());
        return cast(TypeSettingsMapping.class, collection);
    }

    public void delete(Long id) {
        Query query = getPersistenceManager().newQuery(TypeSettingsMapping.class);
        query.setFilter("typeSettingId == id");
        query.declareParameters("java.lang.Long typeSettingId");
        query.setUnique(true);

        TypeSettingsMapping result = (TypeSettingsMapping) query.execute(id);

        if (result != null) {
            getPersistenceManager().deletePersistent(result);
        }
    }
}
