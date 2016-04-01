-- Changing default name of Character from 'char' to 'character'

UPDATE "Type" SET "defaultName"='character' WHERE "typeClass" LIKE 'java.lang.Character'