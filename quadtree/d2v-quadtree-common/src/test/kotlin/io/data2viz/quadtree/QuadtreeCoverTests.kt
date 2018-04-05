package io.data2viz.quadtree

import io.data2viz.test.JsName
import io.data2viz.test.TestBase
import kotlin.test.Test

class QuadtreeCoverTests : TestBase() {

    @Test
    @JsName("quadtree_cover_1")
    fun `quadtree cover(x, y) sets a trivial extent if the extent was undefined`() {
        val quadtree = buildQuadtree()
        quadtree.cover(1.0, 2.0)

        quadtree.extent.toArray() shouldBe arrayOf(1.0, 2.0, 2.0, 3.0)
    }

    @Test
    @JsName("quadtree_cover_2")
    fun `quadtree cover(x, y) sets a non-trivial squarified and centered extent if the extent was trivial`() {
        val quadtree = buildQuadtree()
        quadtree.cover(.0, .0)
        quadtree.cover(1.0, 2.0)

        quadtree.extent.toArray() shouldBe arrayOf(.0, .0, 2.0, 2.0)
    }

    @Test
    @JsName("quadtree_cover_3")
    fun `quadtree cover(x, y) ignores invalid points`() {
        val quadtree = buildQuadtree()
        quadtree.cover(.0, .0)
        quadtree.cover(Double.NaN, 12.0)
        quadtree.cover(12.0, Double.NaN)

        quadtree.extent.toArray() shouldBe arrayOf(.0, .0, 1.0, 1.0)
    }

    @Test
    @JsName("quadtree_cover_5")
    fun `quadtree cover(x, y) repeatedly doubles the existing extent if the extent was non-trivial`() {
        var quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(-1.0, -1.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(-2.0, -2.0, 2.0, 2.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(1.0, -1.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(.0, -2.0, 4.0, 2.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(3.0, -1.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(.0, -2.0, 4.0, 2.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(3.0, 3.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(.0, .0, 4.0, 4.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(1.0, 3.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(.0, .0, 4.0, 4.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(-1.0, 3.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(-2.0, .0, 2.0, 4.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(-1.0, 1.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(-2.0, .0, 2.0, 4.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(-3.0, -3.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(-6.0, -6.0, 2.0, 2.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(3.0, -3.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(.0, -6.0, 8.0, 2.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(5.0, -3.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(.0, -6.0, 8.0, 2.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(5.0, 3.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(.0, .0, 8.0, 8.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(5.0, 5.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(.0, .0, 8.0, 8.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(3.0, 5.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(.0, .0, 8.0, 8.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(-3.0, 5.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(-6.0, .0, 2.0, 8.0)

        quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
            cover(-3.0, 3.0)
        }
        quadtree.extent.toArray() shouldBe arrayOf(-6.0, .0, 2.0, 8.0)
    }

    @Test
    @JsName("quadtree_cover_6")
    @Suppress("UNCHECKED_CAST")
    fun `quadtree cover(x, y) repeatedly wraps the root node if it has children`() {
        var quadtree = buildQuadtree {
            add(arrayOf(0, 0))
            add(arrayOf(2, 2))
        }
        var root = (quadtree.root as InternalNode)
        var toData = root.toData()
        (toData[0] as Array<Int>) shouldBe arrayOf(0, 0)
        toData[1] shouldBe null
        toData[2] shouldBe null
        (toData[3] as Array<Int>) shouldBe arrayOf(2, 2)

        quadtree = buildQuadtree {
            add(arrayOf(0, 0))
            add(arrayOf(2, 2))
            cover(3.0, 3.0)
        }
        root = (quadtree.root as InternalNode)
        toData = root.toData()
        ((toData[0] as Array<Any?>)[0] as Array<Int>) shouldBe arrayOf(0, 0)
        (toData[0] as Array<Any?>)[1] shouldBe null
        (toData[0] as Array<Any?>)[1] shouldBe null
        ((toData[0] as Array<Any?>)[3] as Array<Int>) shouldBe arrayOf(2, 2)
        toData[1] shouldBe null
        toData[2] shouldBe null
        toData[3] shouldBe null

        quadtree = buildQuadtree {
            add(arrayOf(0, 0))
            add(arrayOf(2, 2))
            cover(-3.0, 5.0)
        }
        root = (quadtree.root as InternalNode)
        toData = root.toData()
        toData[0] shouldBe null
        (toData[1] as Array<Any?>)[0] shouldBe null
        (((toData[1] as Array<Any?>)[1] as Array<Any?>)[0] as Array<Int>) shouldBe arrayOf(0, 0)
        ((toData[1] as Array<Any?>)[1] as Array<Any?>)[1] shouldBe null
        ((toData[1] as Array<Any?>)[1] as Array<Any?>)[2] shouldBe null
        (((toData[1] as Array<Any?>)[1] as Array<Any?>)[3] as Array<Int>) shouldBe arrayOf(2, 2)
        (toData[1] as Array<Any?>)[2] shouldBe null
        (toData[1] as Array<Any?>)[3] shouldBe null
        toData[2] shouldBe null
        toData[3] shouldBe null
    }

    /*
    // TODO (maybe..)
tape("quadtree.cover(x, y) repeatedly wraps the root node if it has children", function(test) {
  test.deepEqual(q.copy().cover(-1, 3).root(), [,[{data: [0, 0]},,, {data: [2, 2]}],, ]);
  test.deepEqual(q.copy().cover(3, -1).root(), [,, [{data: [0, 0]},,, {data: [2, 2]}], ]);
  test.deepEqual(q.copy().cover(-1, -1).root(), [,,, [{data: [0, 0]},,, {data: [2, 2]}]]);
  test.deepEqual(q.copy().cover(5, 5).root(), [[[{data: [0, 0]},,, {data: [2, 2]}],,, ],,, ]);
  test.deepEqual(q.copy().cover(5, -3).root(), [,, [,, [{data: [0, 0]},,, {data: [2, 2]}], ], ]);
  test.deepEqual(q.copy().cover(-3, -3).root(), [,,, [,,, [{data: [0, 0]},,, {data: [2, 2]}]]]);
  test.end();
});
*/

    @Test
    @JsName("quadtree_cover_7")
    fun `quadtree cover(x, y) does not wrap the root node if it is a leaf`() {
        val quadtree = buildQuadtree {
            cover(.0, .0)
            add(arrayOf(2, 2))
        }

        (quadtree.root as LeafNode).data shouldBe arrayOf(2, 2)

        var q = quadtree.copy()
        q.cover(3.0, 3.0)
        (q.root as LeafNode).data shouldBe arrayOf(2, 2)

        q = quadtree.copy()
        q.cover(-1.0, 3.0)
        (q.root as LeafNode).data shouldBe arrayOf(2, 2)

        q = quadtree.copy()
        q.cover(3.0, -1.0)
        (q.root as LeafNode).data shouldBe arrayOf(2, 2)

        q = quadtree.copy()
        q.cover(-1.0, -1.0)
        (q.root as LeafNode).data shouldBe arrayOf(2, 2)

        q = quadtree.copy()
        q.cover(5.0, 5.0)
        (q.root as LeafNode).data shouldBe arrayOf(2, 2)

        q = quadtree.copy()
        q.cover(-3.0, 5.0)
        (q.root as LeafNode).data shouldBe arrayOf(2, 2)

        q = quadtree.copy()
        q.cover(-3.0, -3.0)
        (q.root as LeafNode).data shouldBe arrayOf(2, 2)

        q = quadtree.copy()
        q.cover(5.0, -3.0)
        (q.root as LeafNode).data shouldBe arrayOf(2, 2)
    }

    @Test
    @JsName("quadtree_cover_8")
    fun `quadtree cover(x, y) does not wrap the root node if it is undefined`() {
        val quadtree = buildQuadtree {
            cover(.0, .0)
            cover(2.0, 2.0)
        }

        quadtree.root shouldBe null

        var q = quadtree.copy()
        q.cover(3.0, 3.0)
        q.root shouldBe null

        q = quadtree.copy()
        q.cover(-1.0, 3.0)
        q.root shouldBe null

        q = quadtree.copy()
        q.cover(3.0, -1.0)
        q.root shouldBe null

        q = quadtree.copy()
        q.cover(5.0, 5.0)
        q.root shouldBe null
    }
}
