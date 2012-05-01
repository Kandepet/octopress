<?php get_header(); ?>
        <div id="main">
            <div id="content">

            <div class="blog-index">

	<?php if (have_posts()) : ?>

                <article class="hentry">
                    <header>
		            <!--<h1 class="pagetitle">Search Results</h1>-->
		            <h1 class="pagetitle">Search Result for <?php /* Search Count */ $allsearch = &new WP_Query("s=$s&showposts=-1"); $key = wp_specialchars($s, 1); $count = $allsearch->post_count; _e(''); _e('<span class="search-terms">'); echo $key; _e('</span>'); _e(' &mdash; '); echo $count . ' '; _e('articles'); wp_reset_query(); ?></h1>
                    </header>
                </article> 

        <?php while (have_posts()) : the_post(); ?>


                <article class="hentry">
                    <header>
                    <h1 class="entry-title"><a href="<?php the_permalink() ?>"><?php the_title(); ?></a></h1>
                    <p class="meta">
                    <time datetime="2011-07-30T13:34:00-05:00" pubdate  data-updated="true" ><?php the_time('d') ?>, <?php the_time('M y') ?></time></p>
                    </header>

				    <!--<div class="entry">-->
                    <div class="entry-content">
					    <!--<?php the_content('<em>Continue reading &rarr;</em>'); ?>-->
					    <?php the_excerpt(); ?>
                    </div>
                </article> 
				<div class="clearfix"></div>

		<?php endwhile; ?>

		<!--<div class="navigation">
			<div class="alignleft"><?php next_posts_link('&larr; Older Entries') ?></div>
			<div class="alignright"><?php previous_posts_link('Newer Entries &rarr;') ?></div>
			<div class="clearfix"></div>
		</div>-->

	<?php else : ?>
      <div class="post">
		<h2>No posts found. Please try a different search.</h2>
		<?php //get_search_form(); ?>
	  </div>

	<?php endif; ?>

			</div>

             <aside role=sidebar> <?php get_sidebar(); ?> </aside>

                         </div>
                     </div>


<?php get_footer(); ?>
