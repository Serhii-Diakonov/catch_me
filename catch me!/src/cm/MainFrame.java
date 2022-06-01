package cm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

public class MainFrame extends JFrame {

    boolean basket_down = true;
    boolean basket_left = true;
    boolean break_1 = false;
    boolean break_2 = false;
    boolean crack = false;

    static int WIDTH;
    static int HEIGHT;
    static double x_half_1 = 0;
    static double y_half_1 = 0;
    static double x_half_2 = 0;
    static double y_half_2 = 0;

    URL wolf=getClass().getResource("img/wolf/4.png")
//    String wolf = "res/img/wolf/4.png";
    String score;

    static int wolf_x = 100;
    static final int wolf_y = 160;

    ReentrantLock locker = new ReentrantLock();
    EggThread eggs;
    JComponent comp;
    Egg egg;
    Sound snd;

    public MainFrame(String name) {
        super(name);
        /*
         *Встановлення розмірів
         *та положення головного фрейму
         *по середині екрану в залежності від розмірів
         * певного екрану, що визначається індивідуально
         * на кожному ПК
         *
         */
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dem = kit.getScreenSize();
        WIDTH = dem.width / 2;
        HEIGHT = dem.height / 2;
        setSize(495, 390);
        setLocation(WIDTH / 2, HEIGHT / 2);

        /*
         *Додається обробник подій на натискання
         * клавіш
         *
         * 'ArrowUp', 'W' встановлює значення змінної basket_down=false
         * 'ArrowDown', 'S' встановлює значення змінної basket_down=true
         * 'ArrowLeft', 'A' встановлює значення змінної basket_left=true
         * 'ArrowRight', 'D' встановлює значення змінної basket_left=false
         * 'Esc' завершує виконання потоку EggThread eggs
         * 'P' призупинює виконання потоку EggThread eggs
         * 'Enter' продовжує виконання потоку EggThread eggs
         * 'R' перезапускає гру, якщо вона закінчена (виграв або програв)
         *
         * Від значень змінних basket_down та basket_left
         * залежить розташування Вовка. З натисканням відповідної
         * клавіші фрейм перемальовується та зображення вовка змінюється
         *
         * */
        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    /*
                    * встановлює блокування, яке призупиняє
                    * виконання потоку EggThread eggs.
                    * Фактично, грає "встає на паузу".
                    *
                    * */
                    locker.lock();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    locker.unlock();  //блокування знімається (тобто resume|continue)
                }
                if ((e.getKeyCode() == KeyEvent.VK_RIGHT ||
                        e.getKeyCode() == KeyEvent.VK_D)
                        &&!locker.isLocked()) //нижче у кожному методі ще робиться перевірка
                    basket_left = false;        //на блокування: встановлено -- змін не відбувається
                if ((e.getKeyCode() == KeyEvent.VK_LEFT ||
                        e.getKeyCode() == KeyEvent.VK_A)
                        &&!locker.isLocked())
                    basket_left = true;
                if ((e.getKeyCode() == KeyEvent.VK_UP
                        || e.getKeyCode() == KeyEvent.VK_W)
                        &&!locker.isLocked())
                    basket_down = false;
                if ((e.getKeyCode() == KeyEvent.VK_DOWN ||
                        e.getKeyCode() == KeyEvent.VK_S)
                        &&!locker.isLocked())
                    basket_down = true;
                if (basket_down && basket_left) {   //в залежності від значення змінних
                    wolf_x = 100;                   //обирається шлях розташування зображення
                    wolf = getClass().getResource("img/wolf/4.png");    //та його розміщення за віссю оХ
                } else if (basket_down && !basket_left) {
                    wolf_x = 240;
                    wolf = getClass().getResource("img/wolf/3.png");
                } else if (!basket_down && basket_left) {
                    wolf_x = 100;
                    wolf = getClass().getResource("img/wolf/1.png");
                } else {
                    wolf_x = 240;
                    wolf = getClass().getResource("img/wolf/2.png");
                }
                if (e.getKeyCode() == KeyEvent.VK_R && eggs.isOver()) {
                    Game.restart();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    eggs.stop();            //припиняє виконання потоку EggThread
                    Game.finish();          //завершує виконання програми
                }
            }
        });

        //Детальніше про рух яйця нижче
        Image egg_img = new ImageIcon(getClass().getResource("img/egg.png")).getImage(); //отримує зображення яйця
        int egg_w = egg_img.getWidth(null) / 2;
        int egg_h = egg_img.getHeight(null) / 2;

        /*
        * Цей компонент відповідає
        * за усе малювання на фреймі.
        * Протягом виконання програми постійно
        * перемальовується.
        *
        * */
        comp = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                Graphics2D egg_draw = (Graphics2D) g;
                Image background = new ImageIcon(getClass().getResource("img/bg.jpg")).getImage();
//                Image background = new ImageIcon("res/img/bg.jpg").getImage(); //отримує фон
                g2.drawImage(background, 0, 0, null); //малює його
                Image arm = new ImageIcon(getClass().getResource("img/arm.png")).getImage(); //отримує інші елементи
                g2.drawImage(arm, 150, 70, null);

                setFont(new Font("Arial", Font.BOLD, 30)); //встановлює шрифт для очок
                if (eggs.getScore() != 0) score = "" + eggs.getScore();
                else score = "";
                g2.drawString(score, 440, 40);

                Image life = new ImageIcon(getClass().getResource("img/life.png")).getImage();  //малює кількість життів
                int life_offset=300;
                for(int i=0; i<eggs.lifes; i++, life_offset+=40)
                    g2.drawImage(life, life_offset, 100, null);

                Image icon = new ImageIcon(wolf).getImage(); //отримує зображення Вовка
                g2.drawImage(icon, wolf_x, wolf_y, null);

                /*
                * Код виконується, якщо гра не закінчилась
                * та розбилося яйце праворуч або ліворуч.
                * Спочатку малюється вибух, потім
                * дві половинки розлітаються у сторони.
                *
                * Зміна положення відбувається у потоці EggThread.
                * Також там визначається час вибуху, руху половинок
                * та час, поки вони не зникнуть.
                *
                * */
                if ((break_1 || break_2) && !eggs.isOver()) {
                    Image smash = new ImageIcon(getClass().getResource("img/crack.png")).getImage();
                    if (break_1 && crack) g2.drawImage(smash, 98, 290, null);
                    else if (break_2 && crack) g2.drawImage(smash, 359, 290, null);

                    Image img_half_1 = new ImageIcon(getClass().getResource("img/half_egg_2.png")).getImage();
                    Image img_half_2 = new ImageIcon(getClass().getResource("img/half_egg.png")).getImage();
                    if (break_1 || break_2) {
                        g2.translate(x_half_1, y_half_1);
                        g2.drawImage(img_half_1, 0, 0, null);
                        g2.drawImage(img_half_2, (int) (x_half_2 - x_half_1),
                                (int) (y_half_2 - y_half_1), null);
                    }
                }

                /*
                * Якщо яйце є (його не спіймали та воно не розбилося),
                * тоді воно котиться або падає. За це відповідає метод
                * roll_down() класу Egg. При його виклику з потоку
                * EggThread eggs відбувається зміна коефіцієнтів
                * та змінних, відповідних за розташування у фреймі
                *
                * */
                if (egg != null) {
                    egg_draw.translate(egg.getX(), egg.getY());
                    egg_draw.rotate(Math.toRadians(egg.getAngel()), egg_w, egg_h);
                    egg_draw.drawImage(egg_img, 0, 0, null);
                }

                /*
                * Якщо гра закінчена (набрано 50 очок -- перемога,
                * або витрачено усі життя), тоді малюється
                * прямокутник, який відокремлює повідомлення від
                * основного місця дій. Виводиться повідомлення щодо
                * перемоги або поразки, стає доступним перезапуск гри
                *
                * */
                if (eggs.isOver()) {
                    g2.setColor(new Color(0, 0, 0, 80));
                    Rectangle2D rect = new Rectangle2D.Double(0, 0, 500, 500);
                    g2.fill(rect);
                    g2.draw(rect);
                    String text;
                    if (eggs.isWin()) {
                        text = "YOU WIN!";
                    } else {
                        text = "YOU LOOSE(";
                    }
                    g2.setColor(Color.WHITE);
                    Font font = new Font("Arial", Font.BOLD, 30);
                    setFont(font);
                    FontRenderContext cont = g2.getFontRenderContext();
                    Rectangle2D bounds = font.getStringBounds(text, cont);
                    double xx = this.getWidth() / 2 - bounds.getWidth() / 2; //встановлення тексту посередині
                    double yy = this.getHeight() / 2 - bounds.getHeight() / 2;
                    g2.drawString(text, (int) xx, (int) yy);

                    text = "Press `R` to restart, `Ecs` to exit";
                    bounds = font.getStringBounds(text, cont);
                    xx = this.getWidth() / 2 - bounds.getWidth() / 2;
                    yy += 50;
                    g2.drawString(text, (int) xx, (int) yy);
                }
            }
        };
        this.add(comp);
        eggs = new EggThread();
        eggs.start();
        /*
        * Зупиняє відтворення усіх звуків.
        * В принципі зайве, бо поток EggThread
        * чекає закінчення звуків, а основний потік
        * до останнього малює, доки не завершиться
        * потік EggThread.
        *
        * */
        if (eggs.isOver()) snd.stop();
    }

    /*
    * Потік, який відповідає за дії,
    * пов'язані з яйцями та звуками.
    * Не є демоном, бо це основний потік
    * чекає на його завершення (перемальовує себе
    * до останнього, поки цей потік не
    * завершить виконання).
    * Може бути призупинений, відновлений або
    * завершений основним потоком (керування через
    * прослуховувач клавіш).
    *
    * */
    class EggThread extends Thread {
        int ang;
        int score = 0;
        int lifes = 3;
        boolean win = false;

        public EggThread() {
            super();
        }

        @Override
        public void run() {
            long pause = 0;     //потрібно для фіксації
            long waiter = 0;    //проміжку часу між певними подіями
            egg = new Egg(score / 5); //створюється нове яйце
            /*
            * Головний цикл, який відповідає за гру.
            * Він відповідає за те, щоб яйце котилося,
            * розбивалося, ловилося та відтворювалися
            * відповідні звуки. Одночасно може бути
            * лише одне яйце, проте на 10 рівні складності
            * (їх 10, детальніше у класі Egg) піймати усі
            * не дуже легко.
            *
            * Після піймання або розбиття яйця створюється нове.
            *
            * */
            while (lifes > 0 && score < 50) {
                /*
                * Блокувальник намагається отримати блокування.
                * Якщо воно захоплено головним потоком,
                * то цей потік у цьому місці чекає на його
                * звільнення. Коли звільнили -- продовжує виконання.
                *
                * */
                locker.lock();
                locker.unlock();

                if (egg != null) {
                    long start = System.currentTimeMillis(); //встановлення інтервалу для послідовного зсуву
                    while (start + 20 >= System.currentTimeMillis()) {
                    }
                    egg.roll_down();       //котить яйце
                    if (egg.try_catch(basket_left, basket_down)) {  //намагається спіймати яйце (див. кл. Egg)
                        score++;
                        egg = null;
                    }
                    /*
                    * Якщо не спіймав та яйце нижче лінії, то воно розбивається.
                    *
                    * Примітка: лінію, після якої яйце не можна спіймати,
                    * встановлено однакову і для верхнього яйця, і для
                    * нижнього, тобто верхнє яйце можна піймати навіть тоді,
                    * коли воно трохи пролетіло нижче кошика. Щоб це змінити, потрібно
                    * встановити різні лінії для верхнього яйця та нижнього яйця.
                    *
                    * */
                    if (egg != null && egg.getY() >= 290) pause = break_egg();
                    //100 млс -- час, який буде відмальовуватися вибух. Після цього він зникне
                    if ((break_1 || break_2) && pause + 100 <= System.currentTimeMillis()) {
                        double koef = score / 5; //коефіцієнт залежить від рівня складності
                        if (koef == 0) koef = 1; //рівень складності = кількість очок / 5
                        crack = false;
                        //Час, який розлітатимуться половинки яйця. Потім будуть нерухомими.
                        if (pause + 100 <= System.currentTimeMillis() &&
                                pause + 400 / koef > System.currentTimeMillis()) {
                            x_half_1--;
                            y_half_1 -= Math.cos(Math.toDegrees(ang));
                            x_half_2++;
                            y_half_2 -= Math.cos(Math.toDegrees(ang));
                            ang++;
                        }
                        //Після спливання часу половинки одразу зникнуть.
                        if (pause + 1500 / koef <= System.currentTimeMillis()) {
                            ang = 0;
                            break_1 = false;
                            break_2 = false;
                        }
                    }
                    /*
                    * Кожні 2 рівні лунає куряче квоктання.
                    * Щоб не запускалося декілька одразу,
                    * між ними встановлено мінімальний проміжок часу
                    * у 3 сек (тривалість одного квоктання).
                    *
                    * */
                    if (score % 10 == 0 && score > 9 && waiter + 3000 <= System.currentTimeMillis()) {
                        snd = new Sound(new File("res\\sounds\\cock.wav"));
                        snd.play();
                        waiter = System.currentTimeMillis();
                    }
                } else egg = new Egg(score / 5); //нове яйце, якщо попереднє розбилося або спіймали.
                comp.repaint();
                win = isWin();
            }
            //Якщо гра завершилася поразкою, то Вовк говорить.
            if (!win) {
                snd = new Sound(new File("res\\sounds\\game_over.wav"));
                snd.play(true);
                snd.join(); //Чекає, поки Вовк договорить, потім завершується цей потік
                // і головний, бо цей потік припинить до нього звертатися.
            }
        }

        public long break_egg() {
            Sound.playSound("res\\sounds\\break.wav"); //звук биття
            /*
            * В залежності від початкової позиції яйця
            * встановлюються розташування половинок,
            * зменшується кількість життів, запускається
            * відлік для паузи між малюванням биття яйця.
            *
            * */
            if (egg.getPos() == 1 || egg.getPos() == 4) {
                break_1 = true;
                x_half_1 = 98;
                x_half_2 = 98;
            } else {
                break_2 = true;
                x_half_1 = 350;
                x_half_2 = 350;
            }
            y_half_1 = 290;
            y_half_2 = 290;
            crack = true;
            egg = null;
            lifes--;
            return System.currentTimeMillis();
        }

        public int getScore() {
            return score;
        }

        public boolean isWin() {
            return score == 50;
        }

        public boolean isOver() {
            return score == 50 || lifes == 0;
        }
    }
}