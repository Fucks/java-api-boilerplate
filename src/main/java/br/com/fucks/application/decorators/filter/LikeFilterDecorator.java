package br.com.fucks.application.decorators.filter;

public class LikeFilterDecorator {

    private String filter;

    public LikeFilterDecorator(String filter) {
        this.filter = filter;
    }

    public String decorate() {
        return "%" + filter + "%";
    }

}
