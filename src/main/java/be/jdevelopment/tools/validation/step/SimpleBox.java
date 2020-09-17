package be.jdevelopment.tools.validation.step;

class SimpleBox<U> {

    U value = null;
    boolean isNonEmpty = false;

    void reset() {
        this.isNonEmpty = false;
        value = null;
    }

    void set(U value) {
        isNonEmpty = true;
        this.value = value;
    }

}
