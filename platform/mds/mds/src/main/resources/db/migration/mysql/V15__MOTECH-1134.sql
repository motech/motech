-- This migrations adds the length setting to the String type
--
-- insert settings for string type
--

INSERT INTO TypeSetting
SELECT (ts.id + 1), '255', 'mds.form.label.maxTextLength', t.id
FROM TypeSetting ts, Type t
WHERE t.typeClass LIKE 'java.lang.Integer'
ORDER BY ts.id DESC
LIMIT 1;

UPDATE SEQUENCE_TABLE
SET NEXT_VAL = (SELECT id + 1 FROM TypeSetting ORDER BY id DESC LIMIT 1)
WHERE SEQUENCE_NAME LIKE 'org.motechproject.mds.domain.TypeSetting';

--
-- connect settings with string types
--

INSERT INTO TYPE_TYPE_SETTING
SELECT t.id, ts.id, 1
FROM Type t, TypeSetting ts
WHERE t.typeClass LIKE 'java.lang.String' AND ts.name LIKE 'mds.form.label.maxTextLength';

--
-- add validations to the setting
--

INSERT INTO TYPE_SETTING_SETTING_OPTION
SELECT ts.id, 1, 0
FROM TypeSetting ts
WHERE ts.name LIKE 'mds.form.label.maxTextLength'
LIMIT 1;

INSERT INTO TYPE_SETTING_SETTING_OPTION
SELECT ts.id, 2, 1
FROM TypeSetting ts
WHERE ts.name LIKE 'mds.form.label.maxTextLength'
LIMIT 1;
