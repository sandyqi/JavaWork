/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package helloejb1;

import javax.ejb.Local;

/**
 *
 * @author Meng
 */
@Local
public interface HelloLocal {
    public String sayHello(String name);
}
