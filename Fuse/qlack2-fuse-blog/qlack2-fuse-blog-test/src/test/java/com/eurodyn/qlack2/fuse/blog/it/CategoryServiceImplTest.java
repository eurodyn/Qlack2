package com.eurodyn.qlack2.fuse.blog.it;

import com.eurodyn.qlack2.fuse.blog.api.BlogService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCategoryDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogDTO;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import com.eurodyn.qlack2.fuse.blog.api.CategoryService;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class CategoryServiceImplTest extends ITTestConf{

    @Inject
    @Filter(timeout = 1200000)
    CategoryService categoryService;

    @Inject
    @Filter(timeout = 1200000)
    BlogService blogService;

    @Test
    public void createCategory(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogCategoryDTO blogCategoryDTO = TestUtilities.createBlogCategoryDTO(blogID);
        String blogCategoryID = categoryService.createCategory(blogCategoryDTO);
        Assert.assertNotNull(blogCategoryID);
    }

    @Test
    public void createCategories(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogCategoryDTO blogCategoryDTO = TestUtilities.createBlogCategoryDTO(blogID);
        //create list with blogCategoryDTO's
        List<BlogCategoryDTO> blogCategoryDTOs = new ArrayList();
        blogCategoryDTOs.add(blogCategoryDTO);

        categoryService.createCategories(blogCategoryDTOs);
    }

    @Test
    public void editCategory(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogCategoryDTO blogCategoryDTO = TestUtilities.createBlogCategoryDTO(blogID);
        String blogCategoryID = categoryService.createCategory(blogCategoryDTO);
        Assert.assertNotNull(blogCategoryID);

        //edit DTO
        BlogCategoryDTO blogCategoryGetDTO = categoryService.findCategory(blogCategoryID);
        Assert.assertNotNull(blogCategoryGetDTO);

        blogCategoryGetDTO.setDescription("testDesc06");
        blogCategoryGetDTO.setName("testName06");

        categoryService.editCategory(blogCategoryGetDTO);

        Assert.assertEquals("testDesc06",categoryService.findCategory(blogCategoryID).getDescription());
        Assert.assertEquals("testName06",categoryService.findCategory(blogCategoryID).getName());
    }

    @Test
    public void editCategories(){
        BlogDTO blogOneDTO = TestUtilities.createBlogDTO();
        String blogOneID = blogService.createBlog(blogOneDTO);
        Assert.assertNotNull(blogOneID);

        BlogCategoryDTO blogCategoryOneDTO = TestUtilities.createBlogCategoryDTO(blogOneID);
        String blogCategoryOneID = categoryService.createCategory(blogCategoryOneDTO);
        Assert.assertNotNull(blogCategoryOneID);

        //edit DTO
        BlogCategoryDTO blogCategoryOneGetDTO = categoryService.findCategory(blogCategoryOneID);
        Assert.assertNotNull(blogCategoryOneGetDTO);

        blogCategoryOneGetDTO.setDescription("testDesc07");
        blogCategoryOneGetDTO.setName("testName07");

        BlogDTO blogTwoDTO = TestUtilities.createBlogDTO();
        String blogTwoID = blogService.createBlog(blogTwoDTO);
        Assert.assertNotNull(blogTwoID);

        BlogCategoryDTO blogCategoryTwoDTO = TestUtilities.createBlogCategoryDTO(blogTwoID);
        String blogCategoryTwoID = categoryService.createCategory(blogCategoryTwoDTO);
        Assert.assertNotNull(blogCategoryTwoID);

        //edit DTO
        BlogCategoryDTO blogCategoryTwoGetDTO = categoryService.findCategory(blogCategoryTwoID);
        Assert.assertNotNull(blogCategoryTwoGetDTO);

        blogCategoryTwoGetDTO.setDescription("testDesc08");
        blogCategoryTwoGetDTO.setName("testName08");

        //add to list
        List<BlogCategoryDTO> categoriesDTO = new ArrayList();
        categoriesDTO.add(blogCategoryTwoGetDTO);
        categoriesDTO.add(blogCategoryOneGetDTO);

        categoryService.editCategories(categoriesDTO);

        Assert.assertEquals("testDesc07",categoryService.findCategory(blogCategoryOneID).getDescription());
        Assert.assertEquals("testName07",categoryService.findCategory(blogCategoryOneID).getName());
        Assert.assertEquals("testDesc08",categoryService.findCategory(blogCategoryTwoID).getDescription());
        Assert.assertEquals("testName08",categoryService.findCategory(blogCategoryTwoID).getName());
    }

    @Test
    public void deleteCategory(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogCategoryDTO blogCategoryDTO = TestUtilities.createBlogCategoryDTO(blogID);
        String blogCategoryID = categoryService.createCategory(blogCategoryDTO);
        Assert.assertNotNull(blogCategoryID);

        //expected not null
        Assert.assertNotNull(categoryService.findCategory(blogCategoryID));

        categoryService.deleteCategory(blogCategoryID);

        //expected null
        Assert.assertNull(categoryService.findCategory(blogCategoryID));
    }

    @Test
    public void findCategory(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogCategoryDTO blogCategoryDTO = TestUtilities.createBlogCategoryDTO(blogID);
        String blogCategoryID = categoryService.createCategory(blogCategoryDTO);
        Assert.assertNotNull(blogCategoryID);

        Assert.assertNotNull(categoryService.findCategory(blogCategoryID));
    }

}
