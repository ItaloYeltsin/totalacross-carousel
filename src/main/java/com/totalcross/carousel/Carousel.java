package com.totalcross.carousel;

import totalcross.io.IOException;
import totalcross.ui.ClippedContainer;
import totalcross.ui.Control;
import totalcross.ui.MainWindow;
import totalcross.ui.Window;
import totalcross.ui.event.*;
import totalcross.ui.font.Font;
import totalcross.ui.gfx.Color;
import totalcross.ui.gfx.Graphics;
import totalcross.ui.gfx.Rect;
import totalcross.ui.icon.IconType;
import totalcross.ui.icon.MaterialIcons;
import totalcross.ui.image.Image;
import totalcross.ui.image.ImageException;
import totalcross.util.UnitsConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author italo
 *
 * Carousel is a TotalCross GUI Components that dinamically exposes content by immitating the known graphic component
 * carousel. You can add any graphic component to be shown, Control and Containers, then this component will gain the
 * same width and height of Carousel. Try the example bellow:
 * <pre>
 *      Carousel carousel = new Carousel();
 * 		carousel.setAuto(true);
 * 		add(carousel, 0, 0, PARENTSIZE, PARENTSIZE);
 *
 * 		carousel.add(new Container() {
 *                        @Override
 *            public void initUI() {
 * 				backColor = 0x01142F;
 * 				Label label = new Label("Container 1");
 * 				label.transparentBackground = true;
 * 				label.setForeColor(0x748700);
 * 				label.setFont(Font.getFont("Roboto Medium", false, 40));
 * 				label.alphaValue = 10;
 * 				add(label, CENTER, CENTER, PREFERRED, PREFERRED);
 *            }        * 		});
 *
 * 		carousel.add(new Container() {            * 			@Override
 * 			public void initUI() {
 * 				backColor = 0x052555;
 * 				Label label = new Label("Container 2");
 * 				label.transparentBackground = true;
 * 				label.setForeColor(0xD8664D);
 * 				label.setFont(Font.getFont("Roboto Medium", false, 40));
 * 				label.alphaValue = 10;
 * 				add(label, CENTER, CENTER, PREFERRED, PREFERRED);
 *                    }
 * 		});
 *
 * 		carousel.add(new Contai            ) {
 * 			@O            ide
 * 			public void initUI() {
 * 				backColor = 0x002D6D;
 * 				Label label = new Label("Container 3");
 * 				label.transparentBackground = true;
 * 				label.setForeColor(0xFF005C);
 * 				label.setFont(Font.getFont("Roboto Medium", false, 40));
 * 				label.alphaValue = 10;
 * 				add(label, CENTER, CENTER, PREFERRED, PREF            D);
 *        }
 * 		});
 * </pre>
 *
 */
public class Carousel extends ClippedContainer{
    /*** Indicate if a transition animation is happening */
    protected boolean isAnimating = false;
    /** indicates the active */
    protected int activeIndex = 0;
    /** indicates if indicators must be shown */
    protected boolean showIndicators = true;
    /** indicates if buttons must be shown */
    protected boolean showButtons = true;
    /** auto pass to the next child */
    protected boolean auto = false;
    /** time to rotate */
    protected int timeToRotate = 5000;
    /** time ellapsed since the last rotation */
    protected int ellapsedTimeToRotate = 0;
    /** rotate update listener */
    protected UpdateListener rotateUpdate;
    /** transition animator */
    private TransitionAnimator transitionAnimator = new TransitionAnimator(this);
    /** list ordered by insertion time by default */
    private ArrayList<Control> orderedChildren = new ArrayList<>();
    /** adapter */
    private Adapter adapter = new Adapter(this);

    /**
     * Constructor
     */
    public Carousel () {

    }

    /**
     * add a control
     * @param control
     */
    @Override
    public void add(Control control) {
        super.add(control);
        orderedChildren.add(control);
        if(getChildren().length == 1)
            control.setRect(0, 0, PARENTSIZE, PARENTSIZE);
        else
            control.setRect(getWidth(), 0, PARENTSIZE, PARENTSIZE);
    }

    /**
     * remove a control
     * @param control
     */
    @Override
    public void remove(Control control) {
        super.remove(control);
        orderedChildren.remove(control);
    }

    /**
     * add a control at a index
     * @param index
     * @param control
     */
    public void add(int index, Control control) {
        super.remove(control);
        orderedChildren.add(index, control);
    }

    /**
     * add a set of controls
     * @param controls
     */
    public void add(Control ... controls) {
        for (Control c :
                controls) {
            this.add(c);
        }
    }

    /**
     * indicates if a transition is happening
     * @return
     */
    public boolean isAnimating() {
        return isAnimating;
    }

    /**
     * indicates active index
     * @return
     */
    public int getActiveIndex() {
        return activeIndex;
    }

    /**
     * indicates if indicators are being shown
     * @return
     */
    public boolean isIndicatorsShown() {
        return showIndicators;
    }

    /**
     * set if indicators has or not to be shown
     * @param showIndicators
     */
    public void showIndicators(boolean showIndicators) {
        this.showIndicators = showIndicators;
    }

    /**
     * indicates if buttons are being shown
     * @return
     */
    public boolean isButtonsShown() {
        return showButtons;
    }

    /**
     * set buttons to be shown or not
     * @param showButtons
     */
    public void showButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }

    /**
     * indicates auto rotation
     * @return
     */
    public boolean isAuto() {
        return auto;
    }

    /**
     * current time to rotate
     * @return
     */
    public int getTimeToRotate() {
        return timeToRotate;
    }

    /**
     * set time to rotate
     * @param timeToRotate
     */
    public void setTimeToRotate(int timeToRotate) {
        this.timeToRotate = timeToRotate;
    }

    /**
     * get current transition animator instance
     * @return
     */
    public TransitionAnimator getTransitionAnimator() {
        return transitionAnimator;
    }

    /**
     * set a transition animator
     * @param transitionAnimator
     */
    public void setTransitionAnimator(TransitionAnimator transitionAnimator) {
        this.transitionAnimator = transitionAnimator;
    }

    /**
     * get the current adapter
     * @return
     */
    public Adapter getAdapter() {
        return adapter;
    }

    /**
     * set an adapter
     * @param adapter
     */
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    /**
     * set auto rotation (auto rotation means automatically pass to the next child)
     * @param auto
     */
    public void setAuto(boolean auto) {

        if(auto == true && this.auto == false) {
            ellapsedTimeToRotate = 0;
            MainWindow.getMainWindow().addUpdateListener(rotateUpdate = (time) -> {
                if(activeIndex == orderedChildren.size()-1) return;
                ellapsedTimeToRotate += time;
                if(ellapsedTimeToRotate >= timeToRotate) {
                    PenEvent p = new PenEvent();
                    p.x = getWidth();
                    p.type = EventType.PEN_UP;
                    postEvent(p);
                }

            });
        }

        if(auto == false && rotateUpdate != null)
            MainWindow.getMainWindow().removeUpdateListener(rotateUpdate);

        this.auto = auto;
    }

    /**
     * set element at index
     * @param index
     * @param control
     */
    public void setElement(int index, Control control) {
        boolean isAlreadyChild = false;
        for (int i = 0; i < getChildren().length; i++) {
            if(getChildren()[i].equals(control)) {
                isAlreadyChild = true;
                break;
            }
        }
        if(!isAlreadyChild)
            this.add(index, control);
        else
            orderedChildren.set(index, control);
    }

    /**
     * set active index
     * @param activeIndex
     */
    public void setActiveIndex(int activeIndex) {
        orderedChildren.get(activeIndex).setRect(0, 0, getWidth(), getHeight());
        this.activeIndex = activeIndex;
        Window.needsPaint = true;
    }

    /**
     * set animation type
     * @param type
     */
    public void setAnimationType(TransitionType type) {
        transitionAnimator.setAnimationType(type);
    }

    /**
     * set button color
     * @param color
     */
    public void setButtonColor(int color) {
        adapter.setButtonColor(color);
    }

    /**
     * set indicator color
     * @param color
     */
    public void setIndicatorColor(int color) {
        adapter.setIndicatorColor(color);
    }

    /**
     * time spent to animate between transitions in milliseconds
     * @param time
     */
    public void animationTime(int time) {
        transitionAnimator.setAnimationTime(time);
    }

    /**
     * A class that holder bezier parameters and has some known transitions by default
     * @author italo
     */
    public static class TransitionType {
        public static final TransitionType easeInSine = new TransitionType(0.47, 0, 0.745, 0.715);
        public static final TransitionType easeOutSine = new TransitionType(0.39, 0.575, 0.565, 1);
        public static final TransitionType easeInOutSine = new TransitionType(0.445, 0.05, 0.55, 0.95);
        public static final TransitionType easeInQuad = new TransitionType(0.55, 0.085, 0.68, 0.53);
        public static final TransitionType easeOutQuad = new TransitionType(0.25, 0.46, 0.45, 0.94);
        public static final TransitionType easeInOutQuad = new TransitionType(0.455, 0.03, 0.515, 0.955);
        public static final TransitionType easeInCubic = new TransitionType(0.55, 0.055, 0.675, 0.19);
        public static final TransitionType easeOutCubic = new TransitionType(0.215, 0.61, 0.355, 1);
        public static final TransitionType easeInQuart = new TransitionType(0.895, 0.03, 0.685, 0.22);
        public static final TransitionType easeOutQuart = new TransitionType(0.165, 0.84, 0.44, 1);
        public static final TransitionType easeInQuint = new TransitionType(0.755, 0.05, 0.855, 0.06);
        public static final TransitionType easeOutQuint = new TransitionType(0.23, 1, 0.32, 1);
        public static final TransitionType easeInExpo = new TransitionType(0.95, 0.05, 0.795, 0.035);
        public static final TransitionType easeOutExpo = new TransitionType(0.19, 1, 0.22, 1);
        public static final TransitionType easeInOutExpo = new TransitionType(1, 0, 0, 1);
        public static final TransitionType easeInCirc = new TransitionType(0.6, 0.04, 0.98, 0.335);
        public static final TransitionType easeInOutCirc = new TransitionType(0.785, 0.135, 0.15, 0.86);
        public static final TransitionType easeInBack = new TransitionType(0.6, -0.28, 0.735, 0.045);
        public static final TransitionType easeOutBack = new TransitionType(0.175, 0.885, 0.32, 1.275);
        public static final TransitionType easeInOutBack = new TransitionType(0.68, -0.55, 0.265, 1.55);

        public double x1, y1, x2, y2;

        protected TransitionType(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    /**
     * Carousel delegates transition animations to this class
     */
    public class TransitionAnimator {
        /** Carousel instance that has its transistions delegated to this instance */
        Carousel carousel;
        Control inCommingChild = null, outCommingChild = null, inCommingChild2 = null;
        TransitionType transitionType = TransitionType.easeInOutBack;
        Bezier bezier = new Bezier(transitionType.x1, transitionType.y1, transitionType.x2, transitionType.y2);
        int direction = 0;
        int elapssedTime;
        /** Constant that indicates FORWARD ANIMATION */
        public static final int FORWARD = 1;
        /** BACKWORDS animation */
        public static final int BACKWARD = 2;
        protected int animationTime = 500;

        /**
         * Constructor
         * @param carousel
         */
        public TransitionAnimator(Carousel carousel) {
            this.carousel = carousel;
        }

        /**
         * set animation time
         * @param animationTime
         */
        public void setAnimationTime(int animationTime) {
            this.animationTime = animationTime;
        }

        /**
         * set animation type
         * @param transitionType
         */
        public void setAnimationType(TransitionType transitionType) {
            this.transitionType = transitionType;
            bezier = new Bezier(transitionType.x1, transitionType.y1, transitionType.x2, transitionType.y2);
        }

        /**
         * begin an animation
         * @param direction
         */
        public void begin(int direction) {
            if(carousel.getChildren().length < 2 || isAnimating) return;
            isAnimating = true;
            this.direction = direction;
            setMovingChildren();
            elapssedTime = 0;
            final UpdateListener [] updateListener = new UpdateListener[1];

            updateListener[0] = new UpdateListener() {

                public void updateListenerTriggered(int i) {
                    elapssedTime += i;
                    if(animationTime < elapssedTime) {
                        MainWindow.getMainWindow().removeUpdateListener(updateListener[0]);
                        singleStep(animationTime);
                        isAnimating = false;
                        return;
                    }
                    singleStep(elapssedTime);
                }
            };
            MainWindow.getMainWindow().addUpdateListener(updateListener[0]);
        }

        /**
         * set children to be animated
         */
        public void setMovingChildren() {
            List<Control> children = carousel.orderedChildren;
            outCommingChild = children.get(activeIndex);
            inCommingChild = null;
            inCommingChild2 = null; // In case of using easeInOutBack animation the next quickly come in and out.
            if(direction == FORWARD && children.size() > activeIndex + 1) {
                inCommingChild = children.get(activeIndex + 1);
                Rect rect = inCommingChild.getRect();
                rect.x = - rect.width;
                if(children.size() > activeIndex + 2) {
                    inCommingChild2 = children.get(activeIndex + 2);
                    rect = inCommingChild2.getRect();
                    rect.x =  -inCommingChild.getRect().x - rect.width;
                }
                activeIndex++;
            }
            if(direction == BACKWARD && 0 <= activeIndex - 1) {
                inCommingChild = children.get(activeIndex - 1);
                Rect rect = inCommingChild.getRect();
                rect.x = rect.width + carousel.getWidth();
                if(0 <= activeIndex - 2) {
                    inCommingChild2 = children.get(activeIndex - 2);
                    rect = inCommingChild2.getRect();
                    rect.x =  inCommingChild.getRect().x + inCommingChild.getRect().width;
                }
                activeIndex--;
            }

        }

        /**
         * single step animation
         * @param time
         */
        public void singleStep (int time) {
            if(direction == 0) return;
            int inCommingX = 0;
            int inComming2X = 0;
            int outCommingX = 0;
            if(direction == BACKWARD) {
                inCommingX = (int) (updateFunction(time) * getWidth() - getWidth());
                outCommingX = inCommingX + inCommingChild.getWidth();
                if(inCommingChild2 != null) {
                    inComming2X = inCommingX - inCommingChild2.getWidth();
                }
            }
            else {
                inCommingX = - (int) (updateFunction(time) * getWidth() - getWidth());
                outCommingX = inCommingX - outCommingChild.getWidth();
                inComming2X = inCommingX + inCommingChild.getWidth();
            }
            Rect r = inCommingChild.getRect();
            inCommingChild.setRect(inCommingX, r.y, r.width, r.height);
            r = outCommingChild.getRect();
            outCommingChild.setRect(outCommingX, r.y, r.width, r.height);
            if(inCommingChild2 != null) {
                r = inCommingChild2.getRect();
                inCommingChild2.setRect(inComming2X, r.y, r.width, r.height);
            }
            resetSetPositions();
            Window.needsPaint = true;


        }

        /**
         * update function
         * @param time
         * @return
         */
        public double updateFunction(double time) {
            return bezier.getProgression(time/animationTime);
        }

    }

    /**
     * an Extendable class to adapt Carousel to the programmer needs
     */
    public static class Adapter {
        /* indicator images */
        Image disabled, enabled;
        /** carousel instance */
        protected Carousel carousel;
        /** space between indicators */
        protected int space = UnitsConverter.toPixels(DP + 8);
        /** size of indicators */
        protected int size = UnitsConverter.toPixels(DP + 8);
        protected double alpha = 0.5;
        /** x position of active indicator */
        protected int enabledIndicatorX = 0;
        /** y position of indicators */
        protected int indicatorY;
        /** button color */
        protected int buttonColor = Color.WHITE;
        /** indicators color */
        protected int indicatorColor = Color.WHITE;

        /**
         * Constructor
         * @param carousel
         */
        Adapter(Carousel carousel) {
            this.carousel = carousel;
            try {
                disabled = new Image("carousel_indicator_disabled.png").getHwScaledInstance(size, size);
                enabled = new Image("carousel_indicator_enabled.png").getHwScaledInstance(size, size);
            } catch (ImageException | IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * button color
         * @return
         */
        public int getButtonColor() {
            return buttonColor;
        }

        /**
         * set Button Color
         * @param buttonColor
         */
        public void setButtonColor(int buttonColor) {
            this.buttonColor = buttonColor;
        }

        /**
         * get Indicator color
         * @return
         */
        public int getIndicatorColor() {
            return indicatorColor;
        }

        /**
         * set indicator color
         * @param indicatorColor
         */
        public void setIndicatorColor(int indicatorColor) {
            this.indicatorColor = indicatorColor;
            disabled.applyColor2(indicatorColor);
            enabled.applyColor2(indicatorColor);
        }

        /**
         * function called to draw indicators
         * @param g
         */
        public void onDrawIndicators(Graphics g) {
            indicatorY = carousel.getHeight() - UnitsConverter.toPixels(DP + 35);
            int totalsize = carousel.orderedChildren.size()*(size + space) - space;
            int x = (carousel.getWidth() - totalsize) / 2;

            // Draw disabled indicators
            for (int i = 0; i < carousel.orderedChildren.size(); i++) {
                //g.foreColor = g.backColor = 0xE6DAC8;
                g.drawImage(disabled, x + i*(space + size), indicatorY);
            }
            // Draw enabled indicator
            g.drawImage(enabled, x + carousel.activeIndex *(space + size), indicatorY);
        }

        /**
         * function called to draw button
         * @param g
         */
        public void onDrawButtons(Graphics g) {
            Font f = Font.getFont(MaterialIcons._CHEVRON_LEFT.fontName(), false, 24);
            g.setFont(f);
            g.foreColor = buttonColor;
            IconType left = MaterialIcons._CHEVRON_LEFT;

            int height = f.fm.height;
            int margin = UnitsConverter.toPixels(DP + 8);
            int y = (indicatorY - height)/2;

            // LEFT
            g.drawText(left.toString(), margin, y - f.fm.height/2);
            // RIGHT
            IconType right = MaterialIcons._CHEVRON_RIGHT;
            int width = f.fm.height - f.fm.stringWidth(right.toString()); // solution for wrong width
            g.drawText(right.toString(), carousel.getWidth() - width - margin, y - f.fm.height/2);
        }
    }

    @Override
    public void paintChildren() {
        super.paintChildren();
        if(showIndicators) adapter.onDrawIndicators(getGraphics());
        if(showButtons) adapter.onDrawButtons(getGraphics());
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof CarouselEvent) {
            switch (event.type) {
                case CarouselEvent.FORWARD:
                    transitionAnimator.begin(TransitionAnimator.FORWARD);
                    break;
                case CarouselEvent.BACKWARD:
                    transitionAnimator.begin(TransitionAnimator.BACKWARD);
            }
            return;
        }
        switch (event.type) {
            case EventType.PEN_UP:
                PenEvent penEvent = (PenEvent)event;
                if(penEvent.x >= getWidth()*0.75) {
                    if(activeIndex + 1 < getChildren().length) postEvent(new CarouselEvent(CarouselEvent.FORWARD));
                    ellapsedTimeToRotate = 0;
                }
                else if (penEvent.x <= getWidth()*0.25) {
                    if(activeIndex > 0) postEvent(new CarouselEvent(CarouselEvent.BACKWARD));
                    ellapsedTimeToRotate = 0;
                }

        }
    }

    /**
     * Carousel event
     */
    public static class CarouselEvent extends Event {

        public static final int FORWARD = -201;
        public static final int BACKWARD = -202;

        public CarouselEvent(int type) {
            super.type = type;
        }

        @Override
        public void dispatch(EventHandler eventHandler) {

        }
    }
}
