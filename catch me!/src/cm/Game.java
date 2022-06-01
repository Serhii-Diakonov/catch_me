package cm;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.management.ManagementFactory;


public class Game {

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            /*
            *Створюємо головний фрейм,
            * у якому здійснюватиметься малювання
            * */
            JFrame frame = new MainFrame("Catch me!");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    public static void restart(){
        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");

        System.out.println(cmd);

        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(jvmArg + " ");
        }

        System.out.println(cmd);

        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");

        System.out.println(cmd);

        cmd.append(Game.class.getName()).append(" ");

        System.out.println(cmd);

        try{
            Runtime.getRuntime().exec(cmd.toString());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        finish();
    }

    /*
     *Метод завершує виконання програми,
     * коли його викликають.
     * Викликається натисканням клавіши 'Esc'
     * через обробник подій у головному фреймі
     *
     * */
    public static void finish(){
        System.exit(0);
    }
}
