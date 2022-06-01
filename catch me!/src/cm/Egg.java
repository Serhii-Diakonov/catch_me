package cm;

import java.util.Random;


/*
* Цей клас відповідає за технічну
* складову яєць, тобто змінює
* їхнє положення та швидкість в залежності
* від складності, перевіряє, чи можна яйце
* зловити. Оперує окремим яйцем.
* */
public class Egg {

    /*
    * Примітка: тут верхня межа встановлена,
    * проте вона неузгоджена (як і нижня)
    * з межею у потоці EggThread
    * класу MainFrame. Тому яйце, що падає зверху,
    * можна типу підхопити снизу до певного моменту
    * (тобто якщо у верху не зловив, тоді опусти кошик вниз
    * і знову підійми, тоді яйце ніби підхопиться).
    * */
    final static int catch_u = 195;
    final static int catch_d = 257;

    final static int x_l = 40;
    final static int x_r = 420;
    final static int y_u = 120;
    final static int y_d = 197;
    final Random rand = new Random();

    /*
    * Складність (швидкість руху яєць).
    * Усього 10 рівнів.
    * Примітка: після досягнення 10 рівня
    * на наступному (11) складність скидається
    * на 1 рівень і більше не підвищується
    * (див. конструктор).
    *
    * */
    int speed = 1;
    double k_fall = 1;

    private int pos;
    private double x;
    private double y;
    private double shift = 1;
    private double angel = 0;

    public Egg(int level) {
        /*
        * Встановлюється швидкість для яйця
        * в залежності від параметру констркутору
        * (1-10). Далі 1.
        * */
        if(level>1&&level<10)speed=level;
        setPos((rand.nextInt(4)+1)); //випадкова генерація в 1 з 4 позицій
        //позиції йдуть за годинниковою стрілкою. Початок з лівого верхнього кута.
        switch (pos) {
            case 1 -> { //встановлення розташування в залежності від позиції
                setX(x_l);
                setY(y_u);
            }
            case 2 -> {
                setX(x_r);
                setY(y_u);
            }
            case 3 -> {
                setX(x_r);
                setY(y_d);
            }
            case 4 -> {
                setX(x_l);
                setY(y_d);
            }
        }
    }

    public double getX() {
        return x;
    }

    public double getAngel() {
        return angel;
    }

    public double getShift() {
        return shift;
    }

    public double getY() {
        return y;
    }

    public int getPos() {
        return pos;
    }

    public void setAngel(double angel) {
        this.angel = angel;
    }

    public void setShift(double shift) {
        this.shift = shift;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    /*
    * Зсуває та обертає яйце в
    * залежності від позиції та швидкості.
    * Викликається потоком EggThread.
    */
    public void roll_down() {
        switch (pos) {
            case 1 -> {  //діє виконуються аналогічно для кожної з позицій проте з різними параметрами
                /*
                * Якщо обертання завершилося, то скидається
                * і починається спочатку. Питання: чи треба скидати?
                * Чи можно продовжити, а обертання буде здійснюватись
                * за модулем? Вирогідно.
                * */
                if (getAngel() == 361) setAngel(0);
                if (getY() > 157) {
                    setX(getX());           //змін не відбувається, тобто падає прямо вниз
                    /*
                    * Встановлення положення на Оу.
                    * Коли досягається певна межа (157),
                    * яйце починає падати (у задумці було
                    * зробити так, щоб яйце робило дугу, проте
                    * не вийшло, тому воно падає, наче камінь).
                    * k_fall прискорює падіння (яйце розганяється).
                    *
                    * Ще було б непогано покращити фізику падіння,
                    * Щоб траєкторія залежала від швидкості.
                    *
                    * */
                    setY(y_u + shift * speed * k_fall);
                    k_fall += 0.03;
                } else {
                    //Котиться. 1.6 -- коефіцієнт, щоб котилося вздовж поверхні
                    setY(y_u + shift * speed);
                    setX(x_l + 1.6 * shift * speed);
                }
                switch (speed) { //встановлення швидкості обертання в залежності від рівня складності
                    case 1 -> setAngel(getAngel() + 5);
                    case 2 -> setAngel(getAngel() + 7);
                    case 3 -> setAngel(getAngel() + 8.5);
                    case 4 -> setAngel(getAngel() + 10);
                    case 5 -> setAngel(getAngel() + 11.5);
                    case 6 -> setAngel(getAngel() + 13);
                    case 7->setAngel(getAngel() + 15.5);
                    case 8 -> setAngel(getAngel() + 20);
                    case 9->setAngel(getAngel() + 25);
                }
            }
            case 2 -> {
                if (getAngel() == 361) setAngel(0);
                if (getY() > 157) {
                    setX(getX());
                    setY(y_u + shift * speed * k_fall);
                    k_fall += 0.03;
                } else {
                    setY(y_u + shift * speed);
                    setX(x_r - 1.6 * shift * speed);
                }
                switch (speed) {
                    case 1 -> setAngel(getAngel() - 5);
                    case 2 -> setAngel(getAngel() - 7);
                    case 3 -> setAngel(getAngel() - 8.5);
                    case 4 -> setAngel(getAngel() - 10);
                    case 5 -> setAngel(getAngel() - 11.5);
                    case 6 -> setAngel(getAngel() - 13);
                    case 7->setAngel(getAngel() - 15.5);
                    case 8 -> setAngel(getAngel() - 20);
                    case 9->setAngel(getAngel() - 25);
                }
            }
            case 3 -> {
                if (getAngel() == 361) setAngel(0);
                if (getY() > 230) {
                    setX(getX());
                    setY(y_d + shift * speed * k_fall);
                    k_fall += 0.03;
                } else {
                    setY(y_d + shift * speed);
                    setX(x_r - 1.6 * shift * speed);
                }
                switch (speed) {
                    case 1 -> setAngel(getAngel() - 5);
                    case 2 -> setAngel(getAngel() - 7);
                    case 3 -> setAngel(getAngel() - 8.5);
                    case 4 -> setAngel(getAngel() - 10);
                    case 5 -> setAngel(getAngel() - 11.5);
                    case 6 -> setAngel(getAngel() - 13);
                    case 7->setAngel(getAngel() - 15.5);
                    case 8 -> setAngel(getAngel() - 20);
                    case 9->setAngel(getAngel() - 25);
                }
            }
            case 4 -> {
                if (getAngel() == 361) setAngel(0);
                if (getY() > 230) {
                    setX(getX());
                    setY(y_d + shift * speed * k_fall);
                    k_fall += 0.03;
                } else {
                    setY(y_d + shift * speed);
                    setX(x_l + 1.6 * shift * speed);
                }
                switch (speed) {
                    case 1 -> setAngel(getAngel() + 5);
                    case 2 -> setAngel(getAngel() + 7);
                    case 3 -> setAngel(getAngel() + 8.5);
                    case 4 -> setAngel(getAngel() + 10);
                    case 5 -> setAngel(getAngel() + 11.5);
                    case 6 -> setAngel(getAngel() + 13);
                    case 7->setAngel(getAngel() + 15.5);
                    case 8 -> setAngel(getAngel() + 20);
                    case 9->setAngel(getAngel() + 25);
                }
            }
        }
        setShift(getShift() + 0.25); //зсув координат яйця при настопному виклику roll_down()
    }


    /*
    * Намагається спіймати яйце, отримавши
    * поточне розташування (позицію) кошика.
    * Якщо яйце вище межі (????щось не те????)
    * та кошик у відповідній позиції, тоді повертає true (спіймано),
    * інакше повертає false.
    * Викликається потоком EggThread.
    *
    * Примітка: тут верхня межа встановлена,
    * проте вона неузгоджена (як і нижня)
    * з межею у потоці EggThread
    * класу MainFrame. Тому яйце, що падає зверху,
    * можна типу підхопити снизу до певного моменту
    * (тобто якщо у верху не зловив, тоді опусти кошик вниз
    * і знову підійми, тоді яйце ніби підхопиться).
    *
    * */
    public boolean try_catch(boolean left, boolean down) {
        if (pos == 1) return (left && !down && getY() >= catch_u);
        else if (pos == 2) return (!left && !down && getY() >= catch_u);
        else if (pos == 3) return (!left && down && getY() >= catch_d);
        else if (pos == 4) return (left && down && getY() >= catch_d);
        return false;
    }
}