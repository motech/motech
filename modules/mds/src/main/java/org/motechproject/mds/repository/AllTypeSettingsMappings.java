package org.motechproject.mds.repository;

import org.motechproject.mds.domain.AvailableFieldType;
import org.motechproject.mds.domain.SettingOptions;
import org.motechproject.mds.domain.TypeSettings;
import org.motechproject.mds.dto.SettingDto;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The <code>AllTypeSettingsMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.TypeSettings}.
 */
@Repository
public class AllTypeSettingsMappings extends BaseMdsRepository {

    public TypeSettings save(SettingDto setting, AvailableFieldType valueType, AvailableFieldType type) {
        TypeSettings typeSettings;
        List<org.motechproject.mds.dto.SettingOptions> options = setting.getOptions();
        if (options != null) {
            List<SettingOptions> settingOptionses = new ArrayList<>();
            for (org.motechproject.mds.dto.SettingOptions option : options) {
                settingOptionses.add(new SettingOptions(option));
            }

            typeSettings = new TypeSettings(setting.getName(), setting.getValue().toString(),
                    valueType, type, settingOptionses.toArray(new SettingOptions[options.size()]));
        } else {
            typeSettings = new TypeSettings(setting.getName(), setting.getValue().toString(), valueType, type);
        }

        return save(typeSettings);
    }

    public TypeSettings save(TypeSettings typeSettings) {
        return getPersistenceManager().makePersistent(typeSettings);
    }

    public List<TypeSettings> createEmptySettingsInstance(AvailableFieldType type) {
        List<TypeSettings> settingsForType = getSettingsForType(type);

        List<TypeSettings> newInstance = new ArrayList<>();
        for (TypeSettings typeSettings : settingsForType) {
            newInstance.add(typeSettings.copy());
        }

        return newInstance;
    }

    public List<TypeSettings> getSettingsForType(AvailableFieldType type) {
        Query query = getPersistenceManager().newQuery(TypeSettings.class);
        query.setFilter("typeId == type && field == null");
        query.declareParameters("java.lang.Long typeId");

        Collection collection = (Collection) query.execute(type.getId());
        return cast(TypeSettings.class, collection);
    }

    public void delete(Long id) {
        Query query = getPersistenceManager().newQuery(TypeSettings.class);
        query.setFilter("typeSettingId == id");
        query.declareParameters("java.lang.Long typeSettingId");
        query.setUnique(true);

        TypeSettings result = (TypeSettings) query.execute(id);

        if (result != null) {
            getPersistenceManager().deletePersistent(result);
        }
    }
}
