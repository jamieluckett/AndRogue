package com.readinguni.rq016842.javaroguelike.Core;

/**
 * Created by Jamie on 17/04/2017.
 */

public class BitmapButton
{
    public String name;
    public int x, y, width, height;
    public int spriteNo;

    public BitmapButton(String name, int spriteNo)
    {
        this.name = name;
        this.spriteNo = spriteNo;
    }

    /**
     * @param rx
     * @param ry
     * @return True if the buttons bounding box contains the touch
     */
    public boolean checkPress(int rx, int ry)
    {
        boolean xCheck = this.x <= rx && rx <= this.x + width;
        boolean yCheck = this.y <= ry && ry <= this.y + height;
        return xCheck && yCheck;
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}
