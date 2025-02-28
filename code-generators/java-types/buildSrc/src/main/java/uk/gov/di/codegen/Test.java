package uk.gov.di.codegen;

public class Test {
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

    public void test() {
        TypeA a = new BuilderA().withA().build();
        TypeB b = new BuilderB().withB().withA().withB().withA().build();
        TypeC c = new BuilderC().withC().withB().withA().build();
    }
}
