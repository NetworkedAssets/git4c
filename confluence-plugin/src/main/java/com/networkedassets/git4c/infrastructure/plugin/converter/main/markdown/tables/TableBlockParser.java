/*
    Copyright (c) 2015-2016, Atlassian Pty Ltd
    Modified copyright (c) 2017 NetworkedAssets
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this
      list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
    SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
    CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
    OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.tables;

import org.commonmark.ext.gfm.tables.*;
import org.commonmark.node.Block;
import org.commonmark.node.Node;
import org.commonmark.parser.InlineParser;
import org.commonmark.parser.block.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TableBlockParser extends AbstractBlockParser {

    private final static String COL = "\\s*:?-{3,}:?\\s*";
    private final static String W = "\\s*";
    private final static Pattern TABLE_HEADER_SEPARATOR = Pattern.compile(
            // NetworkedAssets' change - allow whitespace before table declaration
            // For single column, require at least one pipe, otherwise it's ambiguous with setext headers
            W + "\\|" + COL + "\\|?\\s*|" +
                    W + COL + "\\|\\s*|" + W + "\\|?(?:" + COL + "\\|)+" + COL + "\\|?\\s*");

    private final TableBlock block = new TableBlock();
    private final List<CharSequence> rowLines = new ArrayList<>();

    private boolean nextIsSeparatorLine = true;
    private String separatorLine = "";

    private TableBlockParser(CharSequence headerLine) {
        rowLines.add(headerLine);
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockContinue tryContinue(ParserState state) {
        if (state.getLine().toString().contains("|")) {
            return BlockContinue.atIndex(state.getIndex());
        } else {
            return BlockContinue.none();
        }
    }

    @Override
    public void addLine(CharSequence line) {
        if (nextIsSeparatorLine) {
            nextIsSeparatorLine = false;
            separatorLine = line.toString();
        } else {
            rowLines.add(line);
        }
    }

    @Override
    public void parseInlines(InlineParser inlineParser) {
        Node section = new TableHead();
        block.appendChild(section);

        List<TableCell.Alignment> alignments = parseAlignment(separatorLine);

        int headerColumns = -1;
        boolean header = true;
        for (CharSequence rowLine : rowLines) {
            List<String> cells = split(rowLine);
            TableRow tableRow = new TableRow();

            if (headerColumns == -1) {
                headerColumns = cells.size();
            }

            // Body can not have more columns than head
            for (int i = 0; i < headerColumns; i++) {
                String cell = i < cells.size() ? cells.get(i) : "";
                TableCell.Alignment alignment = i < alignments.size() ? alignments.get(i) : null;
                TableCell tableCell = new TableCell();
                tableCell.setHeader(header);
                tableCell.setAlignment(alignment);
                inlineParser.parse(cell.trim(), tableCell);
                tableRow.appendChild(tableCell);
            }

            section.appendChild(tableRow);

            if (header) {
                // Format allows only one row in head
                header = false;
                section = new TableBody();
                block.appendChild(section);
            }
        }
    }

    private static List<TableCell.Alignment> parseAlignment(String separatorLine) {
        List<String> parts = split(separatorLine);
        List<TableCell.Alignment> alignments = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            boolean left = trimmed.startsWith(":");
            boolean right = trimmed.endsWith(":");
            TableCell.Alignment alignment = getAlignment(left, right);
            alignments.add(alignment);
        }
        return alignments;
    }

    private static List<String> split(CharSequence input) {
        String line = input.toString().trim();
        if (line.startsWith("|")) {
            line = line.substring(1);
        }
        List<String> cells = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escape) {
                escape = false;
                sb.append(c);
            } else {
                switch (c) {
                    case '\\':
                        escape = true;
                        // Removing the escaping '\' is handled by the inline parser later, so add it to cell
                        sb.append(c);
                        break;
                    case '|':
                        cells.add(sb.toString());
                        sb.setLength(0);
                        break;
                    default:
                        sb.append(c);
                }
            }
        }
        if (sb.length() > 0) {
            cells.add(sb.toString());
        }
        return cells;
    }

    private static TableCell.Alignment getAlignment(boolean left, boolean right) {
        if (left && right) {
            return TableCell.Alignment.CENTER;
        } else if (left) {
            return TableCell.Alignment.LEFT;
        } else if (right) {
            return TableCell.Alignment.RIGHT;
        } else {
            return null;
        }
    }

    public static class Factory extends AbstractBlockParserFactory {

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
            CharSequence line = state.getLine();
            CharSequence paragraph = matchedBlockParser.getParagraphContent();
            if (paragraph != null && paragraph.toString().contains("|") && !paragraph.toString().contains("\n")) {
                CharSequence separatorLine = line.subSequence(state.getIndex(), line.length()).toString();
                if (TABLE_HEADER_SEPARATOR.matcher(separatorLine).matches()) {
                    List<String> headParts = split(paragraph);
                    List<String> separatorParts = split(separatorLine);
                    if (separatorParts.size() >= headParts.size()) {
                        return BlockStart.of(new TableBlockParser(paragraph))
                                .atIndex(state.getIndex())
                                .replaceActiveBlockParser();
                    }
                }
            }
            return BlockStart.none();
        }
    }

}