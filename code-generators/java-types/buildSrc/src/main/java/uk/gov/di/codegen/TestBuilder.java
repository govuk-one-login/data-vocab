package uk.gov.di.codegen;

public class TestBuilder {
    static class CredentialSubject {}

    static class IdentityCheckSubject extends CredentialSubject {}

    static class VerifiableCredential<T extends CredentialSubject> {
        private T credentialSubject;

        public T getCredentialSubject() {
            return credentialSubject;
        }

        public void setCredentialSubject(T credentialSubject) {
            this.credentialSubject = credentialSubject;
        }

        public static class VerifiableCredentialBuilder<T extends CredentialSubject, J extends VerifiableCredential<T>> {
            protected J instance;

            public VerifiableCredentialBuilder<T, J> withCredentialSubject(T credentialSubject) {
                instance.setCredentialSubject(credentialSubject);
                return this;
            }

            public J build() {
                J result = this.instance;
                this.instance = null;
                return result;
            }
        }
    }

    static class IdentityCheckCredential extends VerifiableCredential<IdentityCheckSubject> {
        private String type;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static class IdentityCheckCredentialBuilder extends VerifiableCredentialBuilder<IdentityCheckSubject, IdentityCheckCredential> {
            public IdentityCheckCredentialBuilder() {
                this.instance = new IdentityCheckCredential();
            }

            public IdentityCheckCredentialBuilder withType(String type) {
                this.instance.type = type;
                return this;
            }
        }
    }

    static class Test {
        void test() {
            IdentityCheckCredential credential = new IdentityCheckCredential();
            IdentityCheckSubject credentialSubject = credential.getCredentialSubject();

            new IdentityCheckCredential.IdentityCheckCredentialBuilder().withType("Type").withCredentialSubject(null).build();

//            new VerifiableCredential.VerifiableCredentialBuilder()
//                    .withCredentialSubject(null)
//                    .build();
        }
    }

    static class A<T> {
        public T a() {
            return (T)this;
        }
    }

    static class B<T extends A<? super T>> extends A<T> {
        public T b() {
            return (T)this;
        }
    }

    static class C extends B<C> {
        public C c() {
            return this;
        }
    }

    public static class Experiment {
        void test() {
            C c = new C();
            var y = c.c().a().b().c().b().a();

            B b = new B();


//            NextBuilder builder = new NextBuilder();
//            builder.withExperiment().withAnother();
        }
    }
}
