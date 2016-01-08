package org.motechproject.mds.test.domain.differentbundles;

public enum Animal {
    CAT {
        @Override
        public String makeNoise() {
            return "meow";
        }
    },
    DOG {
        @Override
        public String makeNoise() {
            return "woof";
        }
    },
    DUCK {
        @Override
        public String makeNoise() {
            return "quack";
        }
    };

    public abstract String makeNoise();
}
