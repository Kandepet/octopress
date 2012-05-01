<?php get_header(); ?>
        <div id="main">
            <div id="content">

        <?php if (have_posts()) : while (have_posts()) : the_post(); ?>

            <div class="blog-index">
                <article class="hentry">
                    <header>
                    <h1 class="entry-title"><a href="<?php the_permalink() ?>"><?php the_title(); ?></a></h1>
                    <p class="meta">
                    <time datetime="2011-07-30T13:34:00-05:00" pubdate  data-updated="true" ><?php the_time('d') ?>, <?php the_time('M y') ?></time></p>
                    </header>

				<!--<div class="entry">-->
                <div class="entry-content">
					<!--<?php the_content('<em>Continue reading &rarr;</em>'); ?>-->
					<?php the_content(); ?>
				    <?php wp_link_pages(array('before' => '<p><strong>Pages:</strong> ', 'after' => '</p>', 'next_or_number' => 'number')); ?>
				    <?php the_tags( '<p class="small">Tags: ', ', ', '</p>'); ?>
				</div>
                <!--<div class="clearfix"></div>-->

                    <footer>
                        <p class="meta">
                            <span class="byline author vcard">Posted by <span class="fn"><?php the_author() ?></span></span>
                            <time datetime="2011-07-30T13:34:00-05:00" pubdate  data-updated="true" ><?php the_time('d') ?>, <?php the_time('M y') ?></time></p>
                        </p>

                    </footer>
                    </article>

                    <section>
                        <?php comments_template(); ?>
                    </section>

			</div>

		<?php endwhile; endif; ?>
    <aside role=sidebar> <?php get_sidebar(); ?> </aside>
	 </div>
	</div>

<?php get_sidebar(); ?>

<?php get_footer(); ?>
