package indi.shinado.piping.pipes.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuzzySearchHelper {

    private Map<String, List<String>> map = new HashMap<>();

    public FuzzySearchHelper() {
        List<String> q = new ArrayList<>();
        q.add("q");
        q.add("w");
        q.add("a");
        map.put("q", q);

        List<String> w = new ArrayList<>();
        w.add("w");
        w.add("q");
        w.add("e");
        w.add("s");
        map.put("w", w);

        List<String> e = new ArrayList<>();
        e.add("e");
        e.add("w");
        e.add("r");
        e.add("d");
        map.put("e", e);

        List<String> r = new ArrayList<>();
        r.add("r");
        r.add("e");
        r.add("t");
        r.add("f");
        map.put("r", r);

        List<String> t = new ArrayList<>();
        t.add("t");
        t.add("r");
        t.add("y");
        t.add("g");
        map.put("t", t);

        List<String> y = new ArrayList<>();
        y.add("y");
        y.add("t");
        y.add("u");
        y.add("h");
        map.put("y", y);

        List<String> u = new ArrayList<>();
        u.add("u");
        u.add("y");
        u.add("i");
        u.add("j");
        map.put("u", u);

        List<String> i = new ArrayList<>();
        i.add("i");
        i.add("u");
        i.add("o");
        i.add("k");
        map.put("i", i);

        List<String> o = new ArrayList<>();
        o.add("o");
        o.add("i");
        o.add("p");
        o.add("l");
        map.put("o", o);

        List<String> p = new ArrayList<>();
        p.add("p");
        p.add("o");
        p.add("l");
        map.put("p", p);

        List<String> a = new ArrayList<>();
        a.add("a");
        a.add("q");
        a.add("s");
        a.add("z");
        map.put("a", a);

        List<String> s = new ArrayList<>();
        s.add("s");
        s.add("w");
        s.add("a");
        s.add("d");
        s.add("x");
        map.put("s", s);

        List<String> d = new ArrayList<>();
        d.add("d");
        d.add("e");
        d.add("s");
        d.add("f");
        d.add("c");
        map.put("d", d);

        List<String> f = new ArrayList<>();
        f.add("f");
        f.add("r");
        f.add("d");
        f.add("g");
        f.add("c");
        f.add("v");
        map.put("f", f);

        List<String> g = new ArrayList<>();
        g.add("g");
        g.add("t");
        g.add("f");
        g.add("h");
        g.add("v");
        g.add("b");
        map.put("g", g);

        List<String> h = new ArrayList<>();
        h.add("h");
        h.add("y");
        h.add("g");
        h.add("j");
        h.add("b");
        h.add("n");
        map.put("h", h);

        List<String> j = new ArrayList<>();
        j.add("j");
        j.add("u");
        j.add("h");
        j.add("k");
        j.add("m");
        j.add("n");
        map.put("j", j);

        List<String> k = new ArrayList<>();
        k.add("k");
        k.add("i");
        k.add("j");
        k.add("l");
        k.add("m");
        map.put("k", k);

        List<String> l = new ArrayList<>();
        l.add("l");
        l.add("o");
        l.add("k");
        l.add("m");
        map.put("l", l);

        List<String> z = new ArrayList<>();
        z.add("z");
        z.add("a");
        z.add("s");
        z.add("x");
        map.put("z", z);

        List<String> x = new ArrayList<>();
        x.add("x");
        x.add("s");
        x.add("z");
        x.add("c");
        map.put("x", x);

        List<String> c = new ArrayList<>();
        c.add("c");
        c.add("d");
        c.add("f");
        c.add("x");
        c.add("v");
        map.put("c", c);

        List<String> v = new ArrayList<>();
        v.add("v");
        v.add("f");
        v.add("g");
        v.add("c");
        v.add("b");
        map.put("v", v);

        List<String> b = new ArrayList<>();
        b.add("b");
        b.add("g");
        b.add("h");
        b.add("v");
        b.add("n");
        map.put("b", b);

        List<String> n = new ArrayList<>();
        n.add("n");
        n.add("h");
        n.add("j");
        n.add("b");
        n.add("m");
        map.put("n", n);

        List<String> m = new ArrayList<>();
        m.add("m");
        m.add("j");
        m.add("k");
        m.add("n");
        map.put("m", m);
    }

}
