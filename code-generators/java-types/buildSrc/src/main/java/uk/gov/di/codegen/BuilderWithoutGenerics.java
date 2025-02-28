package uk.gov.di.codegen;

public class BuilderWithoutGenerics {
    public class TypeA {}
    public class TypeB extends TypeA {}
    public class TypeC extends TypeB {}

    public class BuilderA {
        public BuilderA withA() {
            return this;
        }

        public TypeA build() {
            return null;
        }
    }

    public class BuilderB extends BuilderA {
        @Override
        public BuilderB withA() {
            return this;
        }

        @Override
        public TypeB build() {
            return null;
        }

        public BuilderB withB() {
            return this;
        }
    }

    public class BuilderC extends BuilderB {
        @Override
        public BuilderC withA() {
            return this;
        }

        @Override
        public TypeC build() {
            return null;
        }

        @Override
        public BuilderC withB() {
            return this;
        }

        public BuilderC withC() {
            return this;
        }
    }

    public class Hello {
        private String secret;
    }

    public void test() throws Exception {
        var hello = new Hello();
        var field = Hello.class.getDeclaredField("secret");
        field.setAccessible(true);
        field.set(hello, "Hello, world");
    }
}
