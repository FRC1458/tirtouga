/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Solenoid;

/**
 *
 * @author 1458
 */

public class MorseCode implements Runnable
{
    //To Call: (new Thread(new MorseCode())).start();
    public static String sentance;
    public static String code[];
    public static Solenoid cameraLight;
    public static boolean isDone = true;

    public MorseCode(String inputSentance, Solenoid light)
    {
        isDone = false;
        code = new String[inputSentance.length()];
        sentance = inputSentance;
        cameraLight = light;
        convertToMorse();
    }

    public void run()
    {
        cameraLight.set(false);
        try
        {
            Thread.sleep(4200);
        } catch(InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for(int i = 0; i < code.length; i++)
        {
            for(int j = 0; j < code[i].length(); j++)
            {
                if(code[i].charAt(j) == '*')
                {
                    cameraLight.set(true);
                    try
                    {
                        Thread.sleep(600);
                    } catch(InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else if(code[i].charAt(j) == '-')
                {
                    cameraLight.set(true);
                    try
                    {
                        Thread.sleep(1800);
                    } catch(InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else if(code[i].charAt(j) == ' ')
                {
                    cameraLight.set(false);
                    try
                    {
                        Thread.sleep(600);
                    } catch(InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            cameraLight.set(false);
            try
            {
                Thread.sleep(1800);
            } catch(InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        isDone = true;
    }

    private void convertToMorse()
    {
        for(int i = 0; i < sentance.length(); i++)
        {
            if(sentance.charAt(i) == 'A' || sentance.charAt(i) == 'a')
            {
                code[i] = "* -";
            }
            else if(sentance.charAt(i) == 'B' || sentance.charAt(i) == 'b')
            {
                code[i] = "- * * *";
            }
            else if(sentance.charAt(i) == 'C' || sentance.charAt(i) == 'c')
            {
                code[i] = "- * - *";
            }
            else if(sentance.charAt(i) == 'D' || sentance.charAt(i) == 'd')
            {
                code[i] = "- * *";
            }
            else if(sentance.charAt(i) == 'E' || sentance.charAt(i) == 'e')
            {
                code[i] = "*";
            }
            else if(sentance.charAt(i) == 'F' || sentance.charAt(i) == 'f')
            {
                code[i] = "* * - *";
            }
            else if(sentance.charAt(i) == 'G' || sentance.charAt(i) == 'g')
            {
                code[i] = "- - *";
            }
            else if(sentance.charAt(i) == 'H' || sentance.charAt(i) == 'h')
            {
                code[i] = "* * * *";
            }
            else if(sentance.charAt(i) == 'I' || sentance.charAt(i) == 'i')
            {
                code[i] = "* *";
            }
            else if(sentance.charAt(i) == 'J' || sentance.charAt(i) == 'j')
            {
                code[i] = "* - - -";
            }
            else if(sentance.charAt(i) == 'K' || sentance.charAt(i) == 'k')
            {
                code[i] = "- * -";
            }
            else if(sentance.charAt(i) == 'L' || sentance.charAt(i) == 'l')
            {
                code[i] = "* - * *";
            }
            else if(sentance.charAt(i) == 'M' || sentance.charAt(i) == 'm')
            {
                code[i] = "- -";
            }
            else if(sentance.charAt(i) == 'N' || sentance.charAt(i) == 'n')
            {
                code[i] = "- *";
            }
            else if(sentance.charAt(i) == 'O' || sentance.charAt(i) == 'o')
            {
                code[i] = "- - -";
            }
            else if(sentance.charAt(i) == 'P' || sentance.charAt(i) == 'p')
            {
                code[i] = "* - - *";
            }
            else if(sentance.charAt(i) == 'Q' || sentance.charAt(i) == 'q')
            {
                code[i] = "- - * -";
            }
            else if(sentance.charAt(i) == 'R' || sentance.charAt(i) == 'r')
            {
                code[i] = "* - *";
            }
            else if(sentance.charAt(i) == 'S' || sentance.charAt(i) == 's')
            {
                code[i] = "* * *";
            }
            else if(sentance.charAt(i) == 'T' || sentance.charAt(i) == 't')
            {
                code[i] = "-";
            }
            else if(sentance.charAt(i) == 'U' || sentance.charAt(i) == 'u')
            {
                code[i] = "* * -";
            }
            else if(sentance.charAt(i) == 'V' || sentance.charAt(i) == 'v')
            {
                code[i] = "* * * -";
            }
            else if(sentance.charAt(i) == 'W' || sentance.charAt(i) == 'w')
            {
                code[i] = "* - -";
            }
            else if(sentance.charAt(i) == 'X' || sentance.charAt(i) == 'x')
            {
                code[i] = "- * * -";
            }
            else if(sentance.charAt(i) == 'Y' || sentance.charAt(i) == 'y')
            {
                code[i] = "- * - -";
            }
            else if(sentance.charAt(i) == 'Z' || sentance.charAt(i) == 'z')
            {
                code[i] = "- - * *";
            }
            else if(sentance.charAt(i) == '0')
            {
                code[i] = "- - - - -";
            }
            else if(sentance.charAt(i) == '1')
            {
                code[i] = "* - - - -";
            }
            else if(sentance.charAt(i) == '2')
            {
                code[i] = "* * - - -";
            }
            else if(sentance.charAt(i) == '3')
            {
                code[i] = "* * * - -";
            }
            else if(sentance.charAt(i) == '4')
            {
                code[i] = "* * * * -";
            }
            else if(sentance.charAt(i) == '5')
            {
                code[i] = "* * * * *";
            }
            else if(sentance.charAt(i) == '6')
            {
                code[i] = "- * * * *";
            }
            else if(sentance.charAt(i) == '7')
            {
                code[i] = "- - * * *";
            }
            else if(sentance.charAt(i) == '8')
            {
                code[i] = "- - - * *";
            }
            else if(sentance.charAt(i) == '9')
            {
                code[i] = "- - - - *";
            }
            else
            {
                code[i] = " ";
            }
        }
    }
}
