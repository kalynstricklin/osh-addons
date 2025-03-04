/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2021 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.impl.service.consys.mqtt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public class ByteBufferInputStream extends InputStream
{
    ByteBuffer buf;
    int mark = -1;
    

    public ByteBufferInputStream(ByteBuffer buf)
    {
        this.buf = buf;
    }
    
    
    public void setBuffer(ByteBuffer buf)
    {
        this.buf = buf;
        this.mark = -1;
    }


    public int read() throws IOException
    {
        if (!buf.hasRemaining())
            return -1;
        
        return buf.get() & 0xFF;
    }


    public int read(byte[] bytes, int off, int len) throws IOException
    {
        if (!buf.hasRemaining())
        {
            return -1;
        }

        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
    }


    @Override
    public synchronized void mark(int readlimit)
    {
        this.mark = buf.position();
    }


    @Override
    public synchronized void reset() throws IOException
    {
        if (mark > 0)
        {
            buf.position(mark);
            mark = -1;
        }
    }
}
