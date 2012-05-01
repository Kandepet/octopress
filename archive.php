<?php 
get_header(); ?>
        <div id="main">
            <div id="content">

                <div class="blog-index">

		<?php if (have_posts()) : ?>

                    <article class="hentry">
                        <header>
 	                        <?php $post = $posts[0]; // Hack. Set $post so that the_date() works. ?>
 	                        <?php /* If this is a category archive */ if (is_category()) { ?>
		                        <h1 class="entry-title">Category: <?php single_cat_title(); ?></h1>
 	                        <?php /* If this is a tag archive */ } elseif( is_tag() ) { ?>
		                        <h1 class="entry-title">Posts Tagged: <?php single_tag_title(); ?></h1>
 	                        <?php /* If this is a daily archive */ } elseif (is_day()) { ?>
		                        <h1 class="entry-title">Archive for: <?php the_time('F jS, Y'); ?></h1>
 	                        <?php /* If this is a monthly archive */ } elseif (is_month()) { ?>
		                        <h1 class="entry-title">Archive for: <?php the_time('F, Y'); ?></h1>
 	                        <?php /* If this is a yearly archive */ } elseif (is_year()) { ?>
		                        <h1 class="entry-title">Archive for: <?php the_time('Y'); ?></h1>
	                        <?php /* If this is an author archive */ } elseif (is_author()) { ?>
		                        <h1 class="entry-title">Author Archives</h1>
 	                        <?php /* If this is a paged archive */ } elseif (isset($_GET['paged']) && !empty($_GET['paged'])) { ?>
		                        <h1 class="entry-title">Blog Archives</h1>
                            <?php } ?>
                        </header>
                        <div class="entry-content">
                        </div>
                        <footer>
                        </footer>
                    </article>

		<?php while (have_posts()) : the_post(); ?>
		
                <article class="hentry">
                    <header>
                    <h1 class="entry-title"><a href="<?php the_permalink() ?>"><?php the_title(); ?></a></h1>
                    <p class="meta">
                    <time datetime="2011-07-30T13:34:00-05:00" pubdate  data-updated="true" ><?php the_time('d') ?>, <?php the_time('M y') ?></time></p>
                    </header>

                    <div class="entry-content">
					    <?php the_content(); ?>
				        <?php wp_link_pages(array('before' => '<p><strong>Pages:</strong> ', 'after' => '</p>', 'next_or_number' => 'number')); ?>
				        <?php the_tags( '<p class="small">Tags: ', ', ', '</p>'); ?>
				    </div>

                    <footer>
                        <p class="meta">
                            <span class="byline author vcard">Posted by <span class="fn"><?php the_author() ?></span></span>
                            <time datetime="2011-07-30T13:34:00-05:00" pubdate  data-updated="true" ><?php the_time('d') ?>, <?php the_time('M y') ?></time></p>
                        </p>

                    </footer>
                </article>


		<?php endwhile; ?>

	<?php else :

		if ( is_category() ) { // If this is a category archive
			printf("<h2 class='center'>Sorry, but there aren't any posts in the %s category yet.</h2>", single_cat_title('',false));
		} else if ( is_date() ) { // If this is a date archive
			echo("<h2>Sorry, but there aren't any posts with this date.</h2>");
		} else if ( is_author() ) { // If this is a category archive
			$userdata = get_userdatabylogin(get_query_var('author_name'));
			printf("<h2 class='center'>Sorry, but there aren't any posts by %s yet.</h2>", $userdata->display_name);
		} else {
			echo("<h2 class='center'>No posts found.</h2>");
		}
		//get_search_form();

	endif;
?>
		</div>
    <aside role=sidebar> <?php get_sidebar(); ?> </aside>

	</div>
	</div>

<?php get_footer(); ?>
