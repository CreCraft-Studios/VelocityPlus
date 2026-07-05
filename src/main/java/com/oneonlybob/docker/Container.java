package com.oneonlybob.docker;

public class Container {

    private final String name;
    private String label;

    private final Image image;

    private Container(String name, Image image) {
        this.name=name;
        this.image=image;
    }

    private void setLabel(String label) {
        this.label=label;
    }

    public Image getImage() {
        return this.image;
    }

    public String getName() {
        return this.name;
    }

    public String getLabel() {
        return this.label;
    }

    public static Builder builder() {
        return new Builder();
    }

    //Trust me, I'm an engineer! With epic skill and epic gear.
    public static class Builder {
        private boolean tty=false;
        private String name;
        private String label;
        private Image image;

        public Builder enableTTY() {
            this.tty=true;
            return this;
        }

        public Builder setName(String name) {
            this.name=name;
            return this;
        }

        public Builder setLabel(String label) {
            this.label=label;
            return this;
        }

        public Builder setImage(Image image) {
            this.image=image;
            return this;
        }

        public Container build() {

            if(this.name==null)
                throw new IllegalStateException("Container requires a name");

            if (this.image==null)
                throw new IllegalStateException("Container requires an image");

            Container container = new Container(this.name, this.image);

            if (this.label!=null)
                container.setLabel(this.label);

            return container;
        }
    }
}