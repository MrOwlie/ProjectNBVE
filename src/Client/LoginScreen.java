/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author mrowlie
 */
class LoginScreen implements ScreenController{

    Nifty nifty;
    Screen screen;
    
    public boolean authenticate(String username, String password) {
        System.out.println(username + " : " + password);
        return true;
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {
        System.out.println("Startscreen");
    }

    @Override
    public void onEndScreen() {
        System.out.println("Endscreen");
    }
    
}
