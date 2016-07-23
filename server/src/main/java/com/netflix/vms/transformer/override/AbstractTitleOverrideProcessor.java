package com.netflix.vms.transformer.override;

import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public abstract class AbstractTitleOverrideProcessor implements TitleOverrideProcessor {

    protected final String vip;
    protected final String localBlobStore;
    protected final TransformerContext ctx;

    protected AbstractTitleOverrideProcessor(String vip, String localBlobStore, TransformerContext ctx) {
        this.vip = vip;
        this.localBlobStore = localBlobStore;
        this.ctx = ctx;

        mkdir(localBlobStore);
    }

    @Override
    public String getVip() {
        return vip;
    }

    protected File getFile(String type, long version, int topNode) {
        String fileVIP = vip + "_" + type;
        if (localBlobStore != null) {
            return new File(localBlobStore, "vms." + fileVIP + "-titleoverride-" + version + "_" + topNode);
        } else {
            return new File(new HollowBlobFileNamer(fileVIP).getTitleOverrideFileName(version, topNode));
        }
    }

    protected HollowReadStateEngine readStateEngine(File inputFile) throws IOException {
        ctx.getLogger().info(TransformerLogTag.TitleOverride, "Read StateEngine file:{}", inputFile);

        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try (LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(inputFile))) {
            reader.readSnapshot(is);
        }

        return stateEngine;
    }

    protected void writeStateEngine(HollowWriteStateEngine stateEngine, File outputFile) throws IOException {
        ctx.getLogger().info(TransformerLogTag.TitleOverride, "Write StateEngine file:{}", outputFile);

        TitleOverrideHelper.addBlobID(stateEngine, outputFile);
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        try (LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(outputFile))) {
            writer.writeSnapshot(os);
        }
    }

    protected static void mkdir(String dirName) {
        if (dirName == null) return;

        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}