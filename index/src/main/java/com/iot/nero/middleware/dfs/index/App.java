package com.iot.nero.middleware.dfs.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Hello world!
 *
 */
public class App 
{
    public static final Logger logger= LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
         DFSBootstrap dfsBootstrap = new DFSBootstrap();
         dfsBootstrap.start();
    }
}

